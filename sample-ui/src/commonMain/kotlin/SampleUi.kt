import com.soywiz.klock.*
import com.soywiz.korge.*
import com.soywiz.korge.html.*
import com.soywiz.korge.input.*
import com.soywiz.korge.newui.*
import com.soywiz.korge.render.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korgw.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.*
import com.soywiz.korio.file.std.*
import com.soywiz.korio.net.*
import com.soywiz.korio.util.*
import com.soywiz.korma.interpolation.*

suspend fun main() = Korge(quality = GameWindow.Quality.PERFORMANCE, title = "UI") {
	val nativeProcess = NativeProcess(views)

	uiSkin(OtherUISkin()) {
		uiButton(256.0, 32.0) {
			label = "Disabled Button"
			position(128, 128)
			onClick {
				println("CLICKED!")
			}
			disable()
		}
		uiButton(256.0, 32.0) {
			label = "Enabled Button"
			position(128, 128 + 32)
			onClick {
				println("CLICKED!")
				launchImmediately {
					nativeProcess.close()
				}
			}
			enable()
		}
		uiScrollBar(256.0, 32.0, 0.0, 32.0, 64.0) {
			position(64, 64)
			onChange {
				println(it.ratio)
			}
		}
		uiScrollBar(32.0, 256.0, 0.0, 16.0, 64.0) {
			position(64, 128)
			onChange {
				println(it.ratio)
			}
		}

		uiCheckBox {
			position(128, 128 + 64)
		}

		uiComboBox(items = listOf("ComboBox", "World", "this", "is", "a", "list", "of", "elements")) {
			position(128, 128 + 64 + 32)
		}

		uiScrollableArea(config = {
			position(480, 128)
		}) {

			for (n in 0 until 16) {
				uiButton(label = "HELLO $n").position(0, n * 64)
			}
		}

		val progress = uiProgressBar {
			position(64, 32)
			current = 0.5
		}

		launchImmediately {
			while (true) {
				tween(progress::current[1.0], time = 1.seconds, easing = Easing.EASE_IN_OUT)
				tween(progress::current[1.0, 0.0], time = 1.seconds, easing = Easing.EASE_IN_OUT)
			}
		}
	}
}

private val otherColorTransform = ColorTransform(0.7, 0.9, 1.0)
private val OTHER_UI_SKIN_IMG by lazy {
	DEFAULT_UI_SKIN_IMG.withColorTransform(otherColorTransform)
}

private val OtherUISkinOnce = AsyncOnce<UISkin>()

suspend fun OtherUISkin(): UISkin = OtherUISkinOnce {
	//val ui = resourcesVfs["korge-ui.png"].readNativeImage().toBMP32().withColorTransform(otherColorTransform)
	val ui = resourcesVfs["korge-ui.png"].readNativeImage()

	DefaultUISkin.copy(
		normal = ui.sliceWithSize(0, 0, 64, 64),
		hover = ui.sliceWithSize(64, 0, 64, 64),
		down = ui.sliceWithSize(127, 0, 64, 64),
		backColor = DefaultUISkin.backColor.transform(otherColorTransform),
		//font = Html.FontFace.Bitmap(getDebugBmpFontOnce())
		font = Html.FontFace.Bitmap(resourcesVfs["uifont.fnt"].readBitmapFontWithMipmaps())
	)
}

private suspend fun VfsFile.readBitmapFontWithMipmaps(imageFormat: ImageFormat = RegisteredImageFormats, mipmaps: Boolean = true): BitmapFont =
	readBitmapFont(imageFormat).also { it.atlas.mipmaps(mipmaps) }


private class NativeProcess(views: Views) : NativeProcessBase(views) {
}

private open class NativeProcessBase(val views: Views) {
	open suspend fun alert(message: String) = views.gameWindow.alert(message)
	open suspend fun confirm(message: String): Boolean = views.gameWindow.confirm(message)
	open suspend fun openFileDialog(filter: String? = null, write: Boolean = false, multi: Boolean = false) = views.gameWindow.openFileDialog(filter, write, multi)
	open suspend fun browse(url: URL) = views.gameWindow.browse(url)
	open suspend fun close() = views.gameWindow.close()
}

