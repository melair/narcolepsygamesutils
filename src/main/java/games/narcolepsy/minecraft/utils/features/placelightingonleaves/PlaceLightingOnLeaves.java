package games.narcolepsy.minecraft.utils.features.placelightingonleaves;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bell;
import org.bukkit.block.data.type.Lantern;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class PlaceLightingOnLeaves extends BaseFeature implements Listener {

    public PlaceLightingOnLeaves(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "Place Lighting On Leaves";
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) {
            return;
        }

        if (!e.getAction().isRightClick()) {
            return;
        }

        if (!Tag.LEAVES.isTagged(e.getClickedBlock().getType())) {
            return;
        }

        if (e.getItem() == null) {
            return;
        }

        Block place = e.getClickedBlock().getRelative(e.getBlockFace());
        boolean usedOne = false;

        if (e.getItem().getType() == Material.TORCH || e.getItem().getType() == Material.SOUL_TORCH || e.getItem().getType() == Material.REDSTONE_TORCH) {
            Material wallMat = Material.WALL_TORCH;

            switch (e.getItem().getType()) {
                case TORCH:
                    wallMat = Material.WALL_TORCH;
                    break;
                case SOUL_TORCH:
                    wallMat = Material.SOUL_WALL_TORCH;
                    break;
                case REDSTONE_TORCH:
                    wallMat = Material.REDSTONE_WALL_TORCH;
                    break;
            }

            switch (e.getBlockFace()) {
                case NORTH:
                case EAST:
                case SOUTH:
                case WEST:
                    place.setType(wallMat);

                    Directional bs = (Directional) place.getBlockData();
                    bs.setFacing(e.getBlockFace());
                    place.setBlockData(bs);

                    usedOne = true;
                    break;

                case UP:
                    place.setType(e.getItem().getType());
                    usedOne = true;
                    break;
            }
        }

        if (Tag.CANDLES.isTagged(e.getMaterial())) {
            if (e.getBlockFace() == BlockFace.UP) {
                place.setType(e.getItem().getType());
                usedOne = true;
            }
        }

        if (e.getItem().getType() == Material.LANTERN || e.getItem().getType() == Material.SOUL_LANTERN) {
            if (e.getBlockFace() == BlockFace.DOWN) {
                place.setType(e.getItem().getType());

                Lantern bs = (Lantern) place.getBlockData();
                bs.setHanging(true);
                place.setBlockData(bs);
                usedOne = true;
            }
        }

        if (e.getItem().getType() == Material.BELL) {
            if (e.getBlockFace() == BlockFace.DOWN) {
                place.setType(e.getItem().getType());

                Bell bs = (Bell) place.getBlockData();
                bs.setAttachment(Bell.Attachment.CEILING);
                place.setBlockData(bs);
                usedOne = true;
            }
        }

        if (usedOne) {
            ItemStack is = e.getPlayer().getItemInHand();
            if (is != null) {
                if (is.getAmount() > 1) {
                    is.setAmount(is.getAmount() - 1);
                } else {
                    e.getPlayer().setItemInHand(null);
                }
            }
        }
    }
}
