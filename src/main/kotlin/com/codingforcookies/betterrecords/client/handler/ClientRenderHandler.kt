package com.codingforcookies.betterrecords.client.handler

import com.codingforcookies.betterrecords.ID
import com.codingforcookies.betterrecords.ModConfig
import com.codingforcookies.betterrecords.extensions.glMatrix
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.resources.I18n
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.concurrent.thread

@Mod.EventBusSubscriber(modid = ID, value = [Side.CLIENT])
object ClientRenderHandler {

    var strobeLinger = 0f

    var showDownloading = false
    var nowDownloading: String? = null
    var downloadPercent = 0F

    var showPlayingColor = 0
    var showPlaying = false
    var nowPlaying: String? = null

    fun showPlayingWithTimeout(playing: String) {
        nowPlaying = playing
        showPlaying = true
        showPlayingColor = 0

        thread {
            Thread.sleep(3 * 1000)
            showPlaying = false
        }
    }

    @SubscribeEvent
    fun onClientRender(event: TickEvent.RenderTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            val mc = Minecraft.getMinecraft()
            val res = ScaledResolution(mc)
            val width = res.scaledWidth
            val height = res.scaledHeight
            val fontRenderer = mc.fontRenderer
            mc.entityRenderer.setupOverlayRendering()
            if (strobeLinger > 0f) {
                glMatrix {
                    GL11.glDisable(GL11.GL_TEXTURE_2D)
                    GL11.glEnable(GL11.GL_BLEND)

                    GL11.glBegin(GL11.GL_QUADS)
                    GL11.glColor4f(1f, 1f, 1f, strobeLinger)
                    GL11.glVertex2f(width.toFloat(), 0f)
                    GL11.glVertex2f(0f, 0f)
                    GL11.glVertex2f(0f, height.toFloat())
                    GL11.glVertex2f(width.toFloat(), height.toFloat())
                    GL11.glEnd()

                    GL11.glDisable(GL11.GL_BLEND)
                    GL11.glEnable(GL11.GL_TEXTURE_2D)
                }
                strobeLinger -= if (ModConfig.client.flashMode < 3) 0.01f else 0.2f
            }
            if (showDownloading) {
                glMatrix {
                    GL11.glDisable(GL11.GL_TEXTURE_2D)
                    GL11.glTranslatef((width / 2 - 50).toFloat(), (height - height / 4 + 26).toFloat(), 0f)
                    GL11.glEnable(GL11.GL_BLEND)

                    GL11.glBegin(GL11.GL_QUADS)
                    GL11.glColor4f(0f, 0f, 0f, .8f)
                    GL11.glVertex2f(100f, 0f)
                    GL11.glVertex2f(0f, 0f)
                    GL11.glVertex2f(0f, 4f)
                    GL11.glVertex2f(100f, 4f)
                    GL11.glEnd()

                    GL11.glBegin(GL11.GL_QUADS)
                    GL11.glColor4f(1f, 1f, 1f, .5f)
                    GL11.glVertex2f(downloadPercent * 100f, 0f)
                    GL11.glVertex2f(0f, 0f)
                    GL11.glVertex2f(0f, 4f)
                    GL11.glVertex2f(downloadPercent * 100f, 4f)
                    GL11.glEnd()

                    GL11.glDisable(GL11.GL_BLEND)
                    GL11.glEnable(GL11.GL_TEXTURE_2D)
                }
                fontRenderer.drawStringWithShadow(I18n.format("betterrecords.overlay.downloading", nowDownloading), (width / 2 - fontRenderer.getStringWidth(I18n.format("betterrecords.overlay.downloading", nowDownloading)) / 2).toFloat(), (height - height / 4 + 15).toFloat(), 0xFFFF33)
            }

            if (showPlaying) {
                nowPlaying?.let {
                    if (it.startsWith("Error:")) {
                        fontRenderer.drawStringWithShadow(it, (width / 2 - fontRenderer.getStringWidth(it) / 2).toFloat(), (height - height / 4).toFloat(), 0x990000)
                        return
                    } else if (it.startsWith("Info:")) {
                        fontRenderer.drawStringWithShadow(it, (width / 2 - fontRenderer.getStringWidth(it) / 2).toFloat(), (height - height / 4).toFloat(), 0xFFFF33)
                        return
                    }

                    // I don't understand this but it works
                    val f3 = showPlayingColor
                    val l1 = Color.HSBtoRGB(f3 / 50.0f, 0.7f, 0.6f) and 16777215
                    var k1 = (f3 * 255.0f / 20.0f).toInt()
                    if (k1 > 255) k1 = 255

                    fontRenderer.drawStringWithShadow(I18n.format("betterrecords.overlay.nowplaying", it), (width / 2 - fontRenderer.getStringWidth(I18n.format("betterrecords.overlay.nowplaying", it)) / 2).toFloat(), (height - height / 4).toFloat(), l1 + (k1 shl 24 and -16777216))
                }
            }
        }
    }

    @SubscribeEvent
    fun incrementNowPlayingInt(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            if (showPlaying) {
                showPlayingColor += 3
            }
        }
    }
}
