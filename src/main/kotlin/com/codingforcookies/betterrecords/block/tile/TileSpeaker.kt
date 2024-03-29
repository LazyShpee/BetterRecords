package com.codingforcookies.betterrecords.block.tile

import com.codingforcookies.betterrecords.api.connection.RecordConnection
import com.codingforcookies.betterrecords.api.wire.IRecordWire
import com.codingforcookies.betterrecords.block.BlockSpeaker
import com.codingforcookies.betterrecords.extensions.set
import com.codingforcookies.betterrecords.helper.ConnectionHelper
import net.minecraft.nbt.NBTTagCompound

class TileSpeaker : ModTile(), IRecordWire {

    var rotation = 0f

    var size = BlockSpeaker.SpeakerSize.SMALL
        get() {
            // Horrible hacky solution to fix an issue that only seems to occur sometimes.
            // This will get properly fixed in the next major refactoring pass of blocks / tiles.
            // Which is planned very shortly. In order to properly fix the issue, the way I do many things should be refactored
            return try {
                world?.getBlockState(pos)?.getValue(BlockSpeaker.PROPERTYSIZE) ?: BlockSpeaker.SpeakerSize.SMALL
            } catch (e: Exception) {
                BlockSpeaker.SpeakerSize.SMALL
            }
        }

    override var connections = mutableListOf<RecordConnection>()

    override fun getName() = when (size) {
        BlockSpeaker.SpeakerSize.SMALL -> "Small"
        BlockSpeaker.SpeakerSize.MEDIUM -> "Medium"
        BlockSpeaker.SpeakerSize.LARGE -> "Large"
    } + " Speaker"

    override val songRadiusIncrease
        get() = when (size) {
            BlockSpeaker.SpeakerSize.SMALL -> 15F
            BlockSpeaker.SpeakerSize.MEDIUM -> 35F
            BlockSpeaker.SpeakerSize.LARGE -> 70F
        }

    override fun readFromNBT(compound: NBTTagCompound) = compound.run {
        super.readFromNBT(compound)

        rotation = getFloat("rotation")
        connections = ConnectionHelper.unserializeConnections(getString("connections")).toMutableList()
    }

    override fun writeToNBT(compound: NBTTagCompound) = compound.apply {
        super.writeToNBT(compound)

        set("rotation", rotation)
        set("connections", ConnectionHelper.serializeConnections(connections))
    }
}
