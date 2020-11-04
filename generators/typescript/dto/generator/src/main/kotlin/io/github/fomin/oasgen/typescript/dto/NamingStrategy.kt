package io.github.fomin.oasgen.typescript.dto

import io.github.fomin.oasgen.TypeName
import io.github.fomin.oasgen.TypedFragment

interface NamingStrategy {
    fun typeName(typedFragment: TypedFragment): TypeName
}

class DefaultNamingStrategy : NamingStrategy {
    override fun typeName(typedFragment: TypedFragment) = TypeName.toTypeName(typedFragment)
}
