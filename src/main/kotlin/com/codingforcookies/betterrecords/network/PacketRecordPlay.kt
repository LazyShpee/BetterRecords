package com.codingforcookies.betterrecords.network

import com.codingforcookies.betterrecords.api.event.RecordInsertEvent
import com.codingforcookies.betterrecords.api.sound.ISoundHolder
import com.codingforcookies.betterrecords.api.sound.Sound
import com.codingforcookies.betterrecords.item.ModItems
import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class PacketRecordPlay @JvmOverloads constructor(
        var pos: BlockPos = BlockPos(0, 0, 0),
        var dimension: Int = -1,
        var playRadius: Float = -1F,
        var repeat: Boolean = false,
        var shuffle: Boolean = false,
        var recordStack: ItemStack = ItemStack(ModItems.itemNewRecord)
) : IMessage {

    val sounds = mutableListOf<Sound>()

    init {
        sounds.addAll((recordStack.item as ISoundHolder).getSounds(recordStack))
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(pos.x)
        buf.writeInt(pos.y)
        buf.writeInt(pos.z)

        buf.writeInt(dimension)

        buf.writeFloat(playRadius)

        // Write the amount of sounds we're going to send,
        // to rebuild on the other side.
        buf.writeInt(sounds.size)

        sounds.forEach {
            ByteBufUtils.writeUTF8String(buf, it.url)
            ByteBufUtils.writeUTF8String(buf, it.name)
            buf.writeInt(it.size)
            ByteBufUtils.writeUTF8String(buf, it.author)
        }

        buf.writeBoolean(repeat)
        buf.writeBoolean(shuffle)
    }

    override fun fromBytes(buf: ByteBuf) {
        pos = BlockPos(buf.readInt(), buf.readInt(), buf.readInt())

        dimension = buf.readInt()

        playRadius = buf.readFloat()

        val amount = buf.readInt()
        for (i in 1..amount) {
            sounds.add(Sound(
                    ByteBufUtils.readUTF8String(buf),
                    ByteBufUtils.readUTF8String(buf),
                    buf.readInt(),
                    ByteBufUtils.readUTF8String(buf)
            ))
        }

        repeat = buf.readBoolean()
        shuffle = buf.readBoolean()
    }

    class Handler : IMessageHandler<PacketRecordPlay, IMessage> {

        override fun onMessage(message: PacketRecordPlay, ctx: MessageContext): IMessage? {
            with(message) {
                if (shuffle) {
                    sounds.shuffle()
                }

                MinecraftForge.EVENT_BUS.post(RecordInsertEvent(pos, dimension, playRadius, sounds, repeat))
            }

            return null
        }

    }
}
