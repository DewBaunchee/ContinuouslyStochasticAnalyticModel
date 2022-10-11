package domain.component.source

import domain.component.Component
import domain.request.Request

interface Source : Component<String> {

    fun tick(deltaT: Double): Request?
}