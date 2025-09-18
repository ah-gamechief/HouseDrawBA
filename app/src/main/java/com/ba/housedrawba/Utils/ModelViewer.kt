package com.bc.aifloorplansbc.utils
import android.animation.Animator
import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.DecelerateInterpolator
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.SceneView
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.toFloat3
import io.github.sceneview.node.ModelNode
import kotlin.math.cos
import kotlin.math.sin
object ModelViewer {
    private const val TAG = "ModelViewer"
    // Saved references & defaults
    private var lastSceneView: SceneView? = null
    private var lastModelNode: ModelNode? = null

    private var lastElevation: Float = 30f
    private var lastAzimuth: Float = 45f
    private var lastDistance: Float = 3.0f

    private var defaultCameraPos: Float3? = null
    private var defaultTargetPos: Float3? = null

    @JvmStatic
    fun setup(
        sceneView: SceneView,
        assetFileLocation: String,
        scale: Float = 1.0f,
        autoScaleToUnit: Boolean = true,
        elevationDeg: Float = 30f,
        azimuthDeg: Float = 45f,
        distance: Float = 3.0f
    ) {
        Log.d(TAG, "setup() called. asset=$assetFileLocation elev=$elevationDeg azim=$azimuthDeg dist=$distance")

        lastSceneView = sceneView
        lastElevation = elevationDeg
        lastAzimuth = azimuthDeg
        lastDistance = distance

        val modelLoader: ModelLoader = sceneView.modelLoader

        modelLoader.loadModelInstanceAsync(assetFileLocation) { modelInstance ->
            // ensure UI thread
            sceneView.post {
                if (modelInstance == null) {
                    Log.e(TAG, "Model load FAILED for: $assetFileLocation")
                    return@post
                }

                Log.d(TAG, "Model loaded: $assetFileLocation (modelInstance != null)")

                val modelNode = ModelNode(
                    modelInstance,
                    autoAnimate = false,
                    scaleToUnits = if (autoScaleToUnit) 1.0f else null,
                    centerOrigin = null
                )

                if (!autoScaleToUnit && scale != 1.0f) {
                    modelNode.scale = floatArrayOf(scale, scale, scale).toFloat3()
                    Log.d(TAG, "Applied manual scale: $scale")
                }

                // add node
                sceneView.addChildNode(modelNode)
                lastModelNode = modelNode

                // compute camera position (spherical -> cartesian)
                val elevRad = elevationDeg.toRadians()
                val azimRad = azimuthDeg.toRadians()
                val pos = Float3(
                    (distance * cos(elevRad) * sin(azimRad)).toFloat(),
                    (distance * sin(elevRad)).toFloat(),
                    (distance * cos(elevRad) * cos(azimRad)).toFloat()
                )

                // Save defaults
                defaultCameraPos = pos
                defaultTargetPos = modelNode.worldPosition

                Log.d(TAG, "Saved defaultCameraPos=${f3Str(pos)} defaultTargetPos=${f3Str(defaultTargetPos!!)}")

                // apply camera immediately
                sceneView.cameraNode.position = pos
                // ensure camera looks at the model
                try {
                    sceneView.cameraNode.lookAt(modelNode)
                } catch (t: Throwable) {
                    Log.w(TAG, "lookAt failed: ${t.message}")
                }

                // set manipulator to orbit around model
                sceneView.cameraManipulator = SceneView.createDefaultCameraManipulator(
                    orbitHomePosition = pos,
                    targetPosition = modelNode.worldPosition
                )

                Log.d(TAG, "Camera applied and manipulator set. cameraPos=${f3Str(sceneView.cameraNode.position)}")
            }
        }
    }

    // ---------- Reset ----------
    /**
     * Reset camera to the saved default position/target.
     * Call from Java when user presses reset button:
     * ModelViewer.resetCamera(true, 1000);
     */
    @JvmStatic
    fun resetCamera(animated: Boolean = true, duration: Long = 800) {
        Log.d(TAG, "resetCamera() called. animated=$animated duration=$duration")

        val sceneView = lastSceneView
        val modelNode = lastModelNode
        val targetPos = defaultCameraPos
        val lookAtPos = defaultTargetPos ?: modelNode?.worldPosition

        if (sceneView == null) {
            Log.e(TAG, "resetCamera: no SceneView saved (call setup() first)")
            return
        }
        if (modelNode == null) {
            Log.e(TAG, "resetCamera: no model node saved (model may not be loaded yet)")
            return
        }
        if (targetPos == null || lookAtPos == null) {
            Log.e(TAG, "resetCamera: missing default positions. defaultCameraPos=$targetPos defaultTargetPos=$defaultTargetPos")
            return
        }

        // Ensure UI thread
        sceneView.post {
            val start = sceneView.cameraNode.position
            Log.d(TAG, "resetCamera: start=${f3Str(start)} target=${f3Str(targetPos)} lookAt=${f3Str(lookAtPos)}")

            if (!animated) {
                sceneView.cameraNode.position = targetPos
                try {
                    sceneView.cameraNode.lookAt(modelNode)
                } catch (t: Throwable) {
                    Log.w(TAG, "lookAt failed during instant reset: ${t.message}")
                }
                // re-create manipulator with new home
                sceneView.cameraManipulator = SceneView.createDefaultCameraManipulator(
                    orbitHomePosition = sceneView.cameraNode.worldPosition,
                    targetPosition = lookAtPos
                )
                Log.d(TAG, "resetCamera: instant reset applied. camera=${f3Str(sceneView.cameraNode.position)}")
                return@post
            }

            // Animated reset
            val animator = ValueAnimator.ofFloat(0f, 1f).apply {
                this.duration = duration
                interpolator = DecelerateInterpolator()
            }

            animator.addUpdateListener { anim ->
                val t = anim.animatedValue as Float
                val newX = start.x + t * (targetPos.x - start.x)
                val newY = start.y + t * (targetPos.y - start.y)
                val newZ = start.z + t * (targetPos.z - start.z)
                val newPos = Float3(newX, newY, newZ)

                sceneView.cameraNode.position = newPos
                try {
                    sceneView.cameraNode.lookAt(modelNode)
                } catch (tEx: Throwable) {
                    // ignore lookAt errors but log once
                    Log.w(TAG, "lookAt failed during animation: ${tEx.message}")
                }

                // optional per-frame small log (comment out if too verbose)
                // Log.d(TAG, "reset anim t=$t pos=${f3Str(newPos)}")
            }

            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationCancel(p0: Animator) {
                    Log.d(TAG, "resetCamera: animation cancelled")
                }

                override fun onAnimationEnd(p0: Animator) {
                    sceneView.cameraNode.position = targetPos
                    try {
                        sceneView.cameraNode.lookAt(modelNode)
                    } catch (tEx: Throwable) {
                        Log.w(TAG, "lookAt failed at animation end: ${tEx.message}")
                    }

                    sceneView.cameraManipulator = SceneView.createDefaultCameraManipulator(
                        orbitHomePosition = sceneView.cameraNode.worldPosition,
                        targetPosition = lookAtPos
                    )

                    Log.d(TAG, "resetCamera: animation ended. final camera=${f3Str(sceneView.cameraNode.position)}")
                }
                override fun onAnimationRepeat(p0: Animator) {}
                override fun onAnimationStart(p0: Animator) {
                    Log.d(TAG, "resetCamera: animation started")
                }
            })

            animator.start()
        }
    }

    // ---------- Helpers ----------
    private fun f3Str(f: Float3): String = "(${f.x.format(3)}, ${f.y.format(3)}, ${f.z.format(3)})"
    private fun Float.format(decimals: Int): String =
        "%.${decimals}f".format(this)

    private fun Float.toRadians(): Double = Math.toRadians(this.toDouble())
}
