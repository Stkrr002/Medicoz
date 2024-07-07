package com.alpharays.medico.presentation.overlay_screen


import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.alpharays.medico.R
import com.alpharays.medico.medico_utils.MedicoToast

class OverlayService : Service(), OverlayCallback {

    private val windowManager get() = getSystemService(WINDOW_SERVICE) as WindowManager
    private lateinit var windowParams: WindowManager.LayoutParams
    private val composeView by lazy { ComposeView(this@OverlayService) }

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.Theme_Medico)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showOverlay(composeView)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showOverlay(composeView: ComposeView) {
        val layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

        val screenWidthInitial: Int = WindowManager.LayoutParams.WRAP_CONTENT
        val screenWidth: Int = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight: Int = WindowManager.LayoutParams.WRAP_CONTENT
        val desiredWidth = (0.95 * screenWidth).toInt()
        val params = WindowManager.LayoutParams(
            screenWidthInitial,
            screenHeight,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 50

        windowParams = params

        composeView.setContent {
            Overlay(windowManager, params) { updatedLayoutParams ->
                windowManager.updateViewLayout(composeView, updatedLayoutParams)
            }
        }

        /*
        composeView.apply {
            setContent {
                Overlay()
            }

            setOnTouchListener(object : OnTouchListener {
                private var lastAction = 0
                private var initialX = 0
                private var initialY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {

                            //remember the initial position.
                            initialX = params.x
                            initialY = params.y

                            //get the touch location
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            lastAction = event.action
                            return true
                        }

                        MotionEvent.ACTION_UP -> {
                            //As we implemented on touch listener with ACTION_MOVE,
                            //we have to check if the previous action was ACTION_DOWN
                            //to identify if the user clicked the view or not.
                            if (lastAction == MotionEvent.ACTION_DOWN) {
                                //Open the chat conversation click.
                                Toast.makeText(context, "Toast A", Toast.LENGTH_SHORT).show()
                                stopSelf()
                            }
                            lastAction = event.action
                            return true
                        }

                        MotionEvent.ACTION_MOVE -> {
                            //Calculate the X and Y coordinates of the view.
                            params.x = initialX + (event.rawX - initialTouchX).toInt()
                            params.y = initialY + (event.rawY - initialTouchY).toInt()

                            //Update the layout with new X & Y coordinate
                            windowManager.updateViewLayout(composeView, params)
                            lastAction = event.action
                            return true
                        }
                    }
                    return false
                }
            })
        }
         */

        // Trick The ComposeView into thinking we are tracking lifecycle
        val viewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore get() = ViewModelStore()
        }
        val lifecycleOwner = MyLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        // This is required or otherwise the UI will not recompose
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        windowManager.addView(composeView, windowParams)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onButtonClicked(composeView: ComposeView) {
        removeOverlay(composeView)
        stopSelf()
    }

    private fun removeOverlay(composeView: ComposeView) {
        windowManager.removeView(composeView)
    }

    override fun onDestroy() {
        removeOverlay(composeView)
        super.onDestroy()
    }


    // COMPOSABLES STARTING

    @SuppressLint("ModifierFactoryUnreferencedReceiver")
    fun Modifier.draggableOverlay(
        windowManager: WindowManager,
        layoutParams: WindowManager.LayoutParams,
        updateLayoutParams: (WindowManager.LayoutParams) -> Unit
    ): Modifier = composed {
        // State to remember the last position
        var offsetX by remember { mutableFloatStateOf(0.2f) }
        var offsetY by remember { mutableFloatStateOf(0.2f) }

        pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                offsetX += dragAmount.x
                offsetY += dragAmount.y
                layoutParams.x = offsetX.toInt()
                layoutParams.y = offsetY.toInt()
                updateLayoutParams(layoutParams)
            }
        }
    }

    @Composable
    fun Overlay(
        windowManager: WindowManager,
        layoutParams: WindowManager.LayoutParams,
        updateLayoutParams: (WindowManager.LayoutParams) -> Unit
    ) {
        var isOverlayExpanded by remember {
            mutableStateOf(false)
        }
        val painter = painterResource(id = R.drawable.write_prescription)

        if (!isOverlayExpanded) {
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .background(
                        Color(0xFF00554A),
                        RoundedCornerShape(50.dp)
                    )
                    .clip(RoundedCornerShape(50.dp))
                    .draggableOverlay(windowManager, layoutParams, updateLayoutParams)
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                Icon(
                    modifier = Modifier
                        .size(70.dp)
                        .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 3.dp)
                        .clickable(
                            indication = rememberRipple(
                                bounded = false,
                                radius = 0.dp,
                                color = Color.Transparent
                            ),
                            interactionSource = interactionSource
                        ) {
                            isOverlayExpanded = true
                        },
                    painter = painter,
                    contentDescription = "Open prescription",
                    tint = Color.Unspecified
                )
            }
        } else {
            Box(
                modifier = Modifier.draggableOverlay(
                    windowManager,
                    layoutParams,
                    updateLayoutParams
                )
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFF009B87),
                            RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxWidth(0.9f),
                    contentAlignment = Alignment.Center
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        item {
                            ComposableOverlayUpperRow {
                                isOverlayExpanded = false
                            }
                        }
                        item {
                            ComposableOverlayPrescriptionRows()
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun ComposableOverlayUpperRow(onOverlayExpanded: () -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Prescription",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500
                )
            )
            Icon(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .clickable {
//                        removeOverlay(composeView)
                        onOverlayExpanded()
                    },
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.Red
            )
        }
    }


    @Composable
    fun ComposableOverlayPrescriptionRows() {
        ComposableOverlayDrugInfo()
    }


    @Composable
    fun ComposableOverlayDrugInfo(modifier: Modifier = Modifier) {
        val medicineList = remember {
            mutableStateListOf<String>()
        }
        medicineList.add("")

        Card(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .border(0.5.dp, Color(0xFF3B4557), RoundedCornerShape(6.dp))
                .clickable {

                },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            shape = RoundedCornerShape(10.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth().
                heightIn(Dp.Unspecified, 400.dp),
                horizontalAlignment = Alignment.End
            ){
                items(medicineList){
                    MedicineCardComposable(modifier){
                        medicineList.add("")
                    }
                }
            }
        }
    }

    @Composable
    fun MedicineCardComposable(modifier: Modifier, onAddMore: () -> Unit) {
        val medicineNameHeading = "Medicine name"
        val customButtonColors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFB1BDFF),
            contentColor = Color.Black
        )
        val context = LocalContext.current

        var medicineName by remember {
            mutableStateOf("")
        }
        Text(
            modifier = modifier.fillMaxWidth(),
            text = medicineNameHeading,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        )

        OutlinedTextField(
            value = medicineName,
            onValueChange = { newText ->
                medicineName = newText
            },
            modifier = Modifier
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        // Make window focusable
                        windowParams.flags =
                            windowParams.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
                        windowManager.updateViewLayout(composeView, windowParams)
                    } else {
                        // Restore window flags to not focusable after editing is done
                        windowParams.flags =
                            windowParams.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        windowManager.updateViewLayout(composeView, windowParams)
                    }
                },
            label = { Text("Enter medicine name") },
            textStyle = TextStyle(
                fontSize = 12.sp,
                color = Color(0xFF00897B)
            )
        )

        OutlinedButton(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(0.dp, Color.White),
            colors = customButtonColors,
            onClick = {
                MedicoToast.showToast(context, "M : $medicineName")
            }) {
            Text(
                text = "Add more info",
                style = TextStyle(
                    fontWeight = FontWeight.W500,
                    fontSize = 12.sp
                )
            )
        }


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .size(25.dp)
                    .clickable {
                        onAddMore()
                    },
                imageVector = Icons.Default.AddBox,
                contentDescription = "Add more medicine",
                tint = Color.Blue
            )

            Text(
                text = "Add more medicine",
                modifier = Modifier.padding(start = 5.dp),
                style = TextStyle(fontSize = 12.sp),
                color = Color(0xFF00B49C)
            )
        }

    }

    // COMPOSABLES ENDING

    @Preview
    @Composable
    fun OverlayPreview() {
        ComposableOverlayDrugInfo(Modifier)
    }
}


interface OverlayCallback {
    fun onButtonClicked(composeView: ComposeView)
}