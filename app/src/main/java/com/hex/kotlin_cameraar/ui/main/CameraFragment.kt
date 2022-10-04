package com.hex.kotlin_cameraar.ui.main

import ai.deepar.ar.ARErrorType
import ai.deepar.ar.AREventListener
import ai.deepar.ar.CameraResolutionPreset
import ai.deepar.ar.DeepAR
import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.Image
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.Builder
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.common.util.concurrent.ListenableFuture
import com.hex.kotlin_cameraar.R
import com.hex.kotlin_cameraar.databinding.FragmentCameraBinding
import com.hex.kotlin_cameraar.utils.ARSurfaceProvider
import com.hex.kotlin_cameraar.viewmodel.EffectsArViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import java.util.concurrent.ExecutionException


class CameraFragment : Fragment(), AREventListener, SurfaceHolder.Callback {

    private lateinit var binding: FragmentCameraBinding

    // DeepAR
    private var deepAR: DeepAR? = null
    private var currentEffect = 0

    // CameraX
    private var cameraProvideFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var surfaceProvider: ARSurfaceProvider? = null
    private var lensFacing = CameraSelector.LENS_FACING_FRONT

    // Image Buffers
    private var height = 0
    private var width = 0

    private val viewModel: EffectsArViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initializeDeepAR() {
        deepAR = DeepAR(requireContext())
        deepAR?.setLicenseKey(context?.getString(R.string.deep_ar_key))
        deepAR?.initialize(requireContext(), this)
    }

    private fun initializeViewAR(){
        val arSurfaceView = binding.surface
        arSurfaceView.holder?.addCallback(this)

        arSurfaceView.visibility = View.GONE
        arSurfaceView.visibility = View.VISIBLE
    }

    private fun initializeOnClick(){
        binding.btnSwitchEffect.setOnClickListener {
            switchEffect()
        }

        binding.btnTakeCamera.setOnClickListener {
            deepAR?.takeScreenshot()
        }
    }

    private fun cameraArOpen(){
        cameraProvideFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProvideFuture?.addListener(
            {
                try {
                    val cameraProvider = cameraProvideFuture?.get()
                    if (cameraProvider != null){
                        bindImageAnalysis(cameraProvider)
                    }
                } catch (e: ExecutionException){
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun bindImageAnalysis(cameraProvider: ProcessCameraProvider){
        val cameraResolutionPreset = CameraResolutionPreset.P1920x1080
        val orientation = getScreenOrientation()

        if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE ||
            orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        ) {
            width = cameraResolutionPreset.width
            height = cameraResolutionPreset.height
        } else {
            width = cameraResolutionPreset.height
            height = cameraResolutionPreset.width
        }

        val cameraResolution = android.util.Size(width, height)
        val cameraSelector = Builder().requireLensFacing(lensFacing).build()

        val preview: Preview = Preview.Builder().setTargetResolution(cameraResolution).build()

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, cameraSelector, preview)

        if (surfaceProvider == null) {
            surfaceProvider = ARSurfaceProvider(context, deepAR)
        }

        preview.setSurfaceProvider(surfaceProvider)
        surfaceProvider?.isMirror = lensFacing == CameraSelector.LENS_FACING_FRONT
    }

    private fun getScreenOrientation(): Int {
        val rotation: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            rotation = requireActivity().display?.rotation!!
        } else {
            @Suppress("DEPRECATION")
            rotation = requireActivity().windowManager.defaultDisplay.rotation
        }
        val dm = DisplayMetrics()

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = requireActivity().display
            display?.getRealMetrics(dm)
        } else {
            @Suppress("DEPRECATION") val display = requireActivity().windowManager.defaultDisplay
            @Suppress("DEPRECATION") display.getMetrics(dm)
        }

        width = dm.widthPixels
        height = dm.heightPixels

        // if the device's natural orientation is portrait:
        val orientation: Int =
            if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) &&
                height > width ||
                (rotation == Surface.ROTATION_90 ||
                        rotation == Surface.ROTATION_270) && width > height
            ) {
                when (rotation) {
                    Surface.ROTATION_0 -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    Surface.ROTATION_90 -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    Surface.ROTATION_180 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    Surface.ROTATION_270 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            } else {
                when (rotation) {
                    Surface.ROTATION_0 -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    Surface.ROTATION_90 -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    Surface.ROTATION_180 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    Surface.ROTATION_270 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    else -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }
        return orientation
    }

    // LifeCycle Methods
    private fun switchEffect(){
        currentEffect = (currentEffect + 1) % viewModel.getEffectsSize()
        val effectPaths = viewModel.changeEffectsAR(currentEffect)
        deepAR?.switchEffect("effect", effectPaths[0])

        if (effectPaths[1] != " none") {
            val img: InputStream = requireContext().assets.open(effectPaths[1])
            val drawable: Drawable = Drawable.createFromStream(img, null)
            binding.imgThumbnail.setImageDrawable(drawable)
        }
        else {
            binding.imgThumbnail.setImageDrawable(null)
        }
    }

    override fun onStart() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions( requireActivity(), arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO), 1)
        }

        initializeDeepAR()
        initializeViewAR()
        initializeOnClick()
        cameraArOpen()

        Log.i("OnCycle", "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.i("OnCycle", "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.i("OnCycle", "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.i("OnCycle", "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        val cameraProvider: ProcessCameraProvider?

        try {
          cameraProvider = cameraProvideFuture?.get()
          cameraProvider?.unbindAll()
        } catch (e: ExecutionException) {
          e.printStackTrace()
        } catch (e: InterruptedException) {
          e.printStackTrace()
        }

        if (surfaceProvider != null) {
          surfaceProvider!!.stop()
        }

        if (deepAR == null) {
          return
        }
        deepAR?.setAREventListener(null)
        deepAR?.release()
        deepAR = null

        Log.i("OnCycle", "onDestroy")

        super.onDestroy()
    }

    // ARListener Interface
    override fun screenshotTaken(bitmap: Bitmap) {
        val now: CharSequence = DateFormat.format("yyyy_MM_dd_hh_mm_ss", Date())
        try {
            val imageFile = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "image_$now.jpg")
            val outputStream = FileOutputStream(imageFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()
            MediaScannerConnection.scanFile(requireActivity(), arrayOf(imageFile.toString()),
                null, null)
            Toast.makeText(requireActivity(),
                "Screenshot " + imageFile.name.toString() + " saved.",
                Toast.LENGTH_SHORT).show()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun videoRecordingStarted() {}

    override fun videoRecordingFinished() {}

    override fun videoRecordingFailed() {}

    override fun videoRecordingPrepared() {}

    override fun shutdownFinished() {}

    override fun initialized() {
        deepAR?.switchEffect("effect", "none")
    }

    override fun faceVisibilityChanged(p0: Boolean) {}

    override fun imageVisibilityChanged(p0: String?, p1: Boolean) {}

    override fun frameAvailable(p0: Image?) {}

    override fun error(p0: ARErrorType?, p1: String?) {}

    override fun effectSwitched(p0: String?) {}

    // Surface Interface
    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        deepAR?.setRenderSurface(holder.surface, width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (deepAR != null){
            deepAR?.setRenderSurface(null, 0, 0)
        }
    }
}