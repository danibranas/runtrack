package com.muei.apm.runtrack.data.converters

interface Converter<Model, Entity> {
    fun entityToModel(e: Entity): Model
    fun modelToEntity(m: Model): Entity
}