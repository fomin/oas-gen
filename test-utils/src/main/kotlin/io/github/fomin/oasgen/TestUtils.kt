package io.github.fomin.oasgen

import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository
import org.eclipse.jgit.lib.Constants.OBJ_BLOB
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectInserter
import org.eclipse.jgit.lib.TreeFormatter
import org.eclipse.jgit.revwalk.RevWalk
import org.junit.jupiter.api.Assertions.fail
import java.io.ByteArrayOutputStream
import java.io.File

sealed class FileTreeNode

class FileNode(val content: String) : FileTreeNode()

class DirectoryNode(val children: MutableMap<String, FileTreeNode>) : FileTreeNode() {
    fun addFile(path: String, content: String) {
        addFile(path.split("/"), content)
    }

    private fun addFile(path: List<String>, content: String) {
        if (path.size == 1) {
            val fileName = path[0]
            if (children[fileName] != null) error("file $fileName already exists in $this")
            children[fileName] = FileNode(content)
        } else {
            val directoryName = path[0]
            val directoryNode = when (val fileTreeNode = children[directoryName]) {
                null -> {
                    val newDirectoryNode = DirectoryNode(mutableMapOf())
                    children[directoryName] = newDirectoryNode
                    newDirectoryNode
                }
                is FileNode -> error("item $this should be a directory, but it's a file")
                is DirectoryNode -> fileTreeNode
            }
            directoryNode.addFile(path.subList(1, path.size), content)
        }
    }
}

class TreeConverter(private val inserter: ObjectInserter, private val revWalk: RevWalk) {
    private fun toTree(directoryNode: DirectoryNode): ObjectId {
        val treeFormatter = TreeFormatter()

        directoryNode.children.forEach { (name, child) ->
            when (child) {
                is FileNode -> {
                    val fileBlobId = inserter.insert(OBJ_BLOB, child.content.toByteArray())
                    treeFormatter.append(name, revWalk.lookupBlob(fileBlobId))
                }
                is DirectoryNode -> {
                    val childTreeId = toTree(child)
                    treeFormatter.append(name, revWalk.lookupTree(childTreeId))
                }
            }
        }

        return inserter.insert(treeFormatter)
    }

    fun toTree(outputFiles: Iterable<OutputFile>): ObjectId {
        val rootNode = DirectoryNode(mutableMapOf())
        outputFiles.sortedBy {
            it.path
        }.forEach { outputFile ->
            rootNode.addFile(outputFile.path, outputFile.content)
        }
        return toTree(rootNode)
    }

}

class TestUtils {
    companion object {
        fun assertOutputFilesEquals(message: String, expectedOutputFiles: Iterable<OutputFile>, actualOutputFiles: Iterable<OutputFile>) {
            val repository = InMemoryRepository.Builder().setRepositoryDescription(DfsRepositoryDescription("repo1")).build()
            val inserter = repository.newObjectInserter()
            val revWalk = RevWalk(repository)
            val treeConverter = TreeConverter(inserter, revWalk)
            val expectedTreeId = treeConverter.toTree(expectedOutputFiles)
            val actualTreeId = treeConverter.toTree(actualOutputFiles)
            inserter.flush()
            val outputStream = ByteArrayOutputStream()
            DiffFormatter(outputStream).use { formatter ->
                formatter.setRepository(repository)
                formatter.isDetectRenames = true
                formatter.format(expectedTreeId, actualTreeId)
            }

            if (outputStream.size() > 0) {
                fail<Nothing>("$message\n${String(outputStream.toByteArray())}")
            }
        }
    }
}

fun testCase(
        writer: Writer<OpenApiSchema>,
        baseDir: File,
        schemaPath: String,
        outputDir: File
) {
    val fragmentRegistry = FragmentRegistry(baseDir)
    val rootFragment = fragmentRegistry.get(Reference.root(schemaPath))
    val openApiSchema = OpenApiSchema(rootFragment, null)
    val actualOutputFiles = writer.write(listOf(openApiSchema))

    val outputDirUri = outputDir.toURI()
    val expectedOutputFiles = outputDir.walk().filter { it.isFile }.map {
        val relativePath = outputDirUri.relativize(it.toURI())
        OutputFile(relativePath.toString(), it.readText().replace("\r", ""))
    }.toList()

    TestUtils.assertOutputFilesEquals(
            "Failed test case '${baseDir}'",
            expectedOutputFiles,
            actualOutputFiles
    )
}
