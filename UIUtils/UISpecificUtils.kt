import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build.VERSION.SDK_INT
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object UISpecificUtils {


    /**
     * Screen Utils
     */

    //for edge-to-edge display Android 15+
    fun View.addSystemWindowInsetToPadding(
        top: Boolean = false,
        bottom: Boolean = false
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = if (top) systemBars.top else view.paddingTop,
                bottom = if (bottom) systemBars.bottom else view.paddingBottom
            )
            insets
        }
    }




    /**
     * Keyboard Utils
     */

    fun View.showKeyboard() {
        this.requestFocus()
        if (SDK_INT >= 30){
            val controller = WindowInsetsControllerCompat(
                (this.context as Activity).window,
                this
            )
            controller.show(WindowInsetsCompat.Type.ime())
        }else{
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun View.hideKeyboard() {
        if (SDK_INT >= 30){
            val controller = WindowInsetsControllerCompat(
                (this.context as Activity).window,
                this
            )
            controller.hide(WindowInsetsCompat.Type.ime())
        }else{
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    //on keyboard visibility change
    fun View.setKeyboardVisibilityListener(onVisibilityChanged: (isVisible: Boolean) -> Unit) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
            val isVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            onVisibilityChanged(isVisible)
            insets
        }
    }



    /**
     * Toast Utils
     */

    private var toast: Toast? = null

    fun Context.showShortToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        toast?.cancel()
        toast = Toast.makeText(this, message, duration)
        toast?.show()
    }

    fun Context.showLongToast(message: String, duration: Int = Toast.LENGTH_LONG) {
        toast?.cancel()
        toast = Toast.makeText(this, message, duration)
        toast?.show()
    }



    /**
     * Density Utils
     */

    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density .toInt())

    val Float.dp: Float
        get() = (this * Resources.getSystem().displayMetrics.density)

    val Int.sp: Float
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        )

    val Int.pxToDp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()

    val Int.pxToSp: Float
        get() = this / Resources.getSystem().displayMetrics.scaledDensity



    /*
    * Visibility Controller
    */

    fun View.visible(animate: Boolean = false){
        if (animate){
            this.apply {
                visibility = View.VISIBLE
                alpha = 0f
                animate()
                    .setDuration(300L)
                    .alpha(1f)
                    .start()
            }
        }else{
            this.visibility = View.VISIBLE
        }
    }

    fun View.gone(animate: Boolean = false){
        if (animate){
            this.apply {
                animate()
                    .alpha(0f)
                    .setDuration(300L)
                    .withEndAction { visibility = View.GONE }
                    .start()
            }
        }else{
            this.visibility = View.GONE
        }
    }

    /*
    * Multiple Clicks Handling
    */

    class DebounceClickListener(
        private val interval: Long,
        private val clickCallback:(View) ->Unit
    ): View.OnClickListener{

        private var lastClickTime: Long = 0

        override fun onClick(view: View?) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > interval){
                lastClickTime = currentTime
                view?.let { clickCallback(it) }
            }
        }
    }

    fun View.onSafeClickListener(interval: Long = 1000L, clickCallback:(View) ->Unit){
        this.setOnClickListener(DebounceClickListener(interval, clickCallback))
    }


}
