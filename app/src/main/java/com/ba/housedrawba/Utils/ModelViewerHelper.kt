package com.bc.aifloorplansbc.utils
import android.content.Context
import com.google.android.filament.Engine
import com.google.android.filament.gltfio.FilamentInstance
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.SceneView
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
object ModelViewerHelper {
    fun loadGlb(context: Context, sceneView: SceneView, assetPath: String) {
        val engine: Engine = sceneView.engine

        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val materialLoader = MaterialLoader(engine, context, scope)
        val modelLoader = ModelLoader(engine, context, scope)

        scope.launch {
            val modelInstance: FilamentInstance? = modelLoader.loadModelInstance(assetPath)
            modelInstance?.let {
                val modelNode = ModelNode(
                    modelInstance,
                    autoAnimate = true,
                    scaleToUnits = 1.0f,
                    centerOrigin = Float3(0f, 0f, 0f)
                ).apply {
                    scale = Float3(0.5f, 0.5f, 0.5f)
                    position = Float3(0f, 0f, -3f)
                }

                sceneView.addChildNode(modelNode)
            }
        }
    }
}
