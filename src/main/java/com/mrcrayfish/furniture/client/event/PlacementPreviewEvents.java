package com.mrcrayfish.furniture.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.block.DivingBoardBlock;
import com.mrcrayfish.furniture.block.FreezerBlock;
import com.mrcrayfish.furniture.legacy.bathroom.TwoPartBlock;
import com.mrcrayfish.furniture.legacy.decor.ModernBedBlock;
import com.mrcrayfish.furniture.legacy.decor.ModernSlidingDoorBlock;
import com.mrcrayfish.furniture.legacy.event.BlockPart;
import com.mrcrayfish.furniture.legacy.event.ChoppedLogItem;
import com.mrcrayfish.furniture.legacy.event.GrandChairBlock;
import com.mrcrayfish.furniture.legacy.event.PresentItem;
import com.mrcrayfish.furniture.legacy.event.TreeBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Thin white wireframe of the model of the block about to be placed, when holding a block of this mod.
 * Asks the block for its placement state, so it follows the direction of the player and the connections.
 * A block without a baked model, drawn by its renderer only, shows the edges of its shape instead.
 */
public class PlacementPreviewEvents
{
    private static final float ALPHA = 0.9F;

    /* The edges only have to be worked out once. Keeps the model to notice a reload of the resources. */
    private final Map<BlockState, ModelEdges> edgesCache = new HashMap<>();

    @SubscribeEvent
    public void onRenderHighlight(RenderHighlightEvent.Block event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        if(player == null || level == null)
            return;

        InteractionHand hand = getPlacingHand(player);
        if(hand == null)
            return;

        ItemStack stack = player.getItemInHand(hand);
        BlockItem item = (BlockItem) stack.getItem();
        BlockHitResult target = event.getTarget();
        BlockState targetState = level.getBlockState(target.getBlockPos());

        // Those clicks do something else than placing the block
        if(item instanceof PresentItem || (item instanceof ChoppedLogItem && targetState.is(item.getBlock())))
            return;
        if(!player.isSecondaryUseActive() && targetState.getMenuProvider(level, target.getBlockPos()) != null)
            return;

        BlockPlaceContext context = item.updatePlacementContext(new BlockPlaceContext(player, hand, stack, target));
        if(context == null || !context.canPlace())
            return;

        BlockPos pos = context.getClickedPos();
        BlockState state = item.getBlock().getStateForPlacement(context);
        CollisionContext collision = CollisionContext.of(player);
        if(state == null || !state.canSurvive(level, pos) || !level.isUnobstructed(state, pos, collision))
            return;
        if(state.getBlock() instanceof TwoPartBlock block && !block.canPlaceOtherPart(context, state))
            return;

        Vec3 camera = event.getCamera().getPosition();
        VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.lines());
        PoseStack.Pose pose = event.getPoseStack().last();
        this.drawBlock(consumer, pose, state, state.getShape(level, pos, collision), pos, camera);

        BlockPos otherPos = getOtherPartPos(state, pos, player);
        BlockState otherState = getOtherPartState(state, player);
        if(otherPos != null && otherState != null)
        {
            this.drawBlock(consumer, pose, otherState, otherState.getShape(level, otherPos, collision), otherPos, camera);
        }
    }

    /**
     * The hand that would place a block of this mod: the main hand goes first, like a right click.
     */
    @Nullable
    private static InteractionHand getPlacingHand(LocalPlayer player)
    {
        InteractionHand hand = player.getMainHandItem().getItem() instanceof BlockItem ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        if(player.getItemInHand(hand).getItem() instanceof BlockItem item)
        {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            return id != null && id.getNamespace().equals(Reference.MOD_ID) ? hand : null;
        }
        return null;
    }

    /** Where {@code setPlacedBy} of the block puts its second part. */
    @Nullable
    private static BlockPos getOtherPartPos(BlockState state, BlockPos pos, LocalPlayer player)
    {
        Block block = state.getBlock();
        if(block instanceof TwoPartBlock twoPart)
            return twoPart.getOtherPartPos(state, pos);
        if(block instanceof ModernBedBlock)
            return pos.relative(state.getValue(ModernBedBlock.FACING));
        if(block instanceof DivingBoardBlock)
            return pos.relative(player.getDirection());
        if(block instanceof ModernSlidingDoorBlock || block instanceof GrandChairBlock || block instanceof TreeBlock || block instanceof FreezerBlock)
            return pos.above();
        return null;
    }

    @Nullable
    private static BlockState getOtherPartState(BlockState state, LocalPlayer player)
    {
        Block block = state.getBlock();
        if(block instanceof TwoPartBlock)
            return state.setValue(TwoPartBlock.PART, TwoPartBlock.Part.TOP);
        if(block instanceof ModernBedBlock)
            return state.setValue(ModernBedBlock.PART, BedPart.HEAD);
        if(block instanceof DivingBoardBlock)
            return state.setValue(DivingBoardBlock.PART, com.mrcrayfish.furniture.block.DivingBoardBlock.DivingBoardPart.BOARD).setValue(DivingBoardBlock.DIRECTION, player.getDirection());
        if(block instanceof ModernSlidingDoorBlock)
            return state.setValue(ModernSlidingDoorBlock.HALF, DoubleBlockHalf.UPPER);
        if(block instanceof GrandChairBlock || block instanceof TreeBlock)
            return state.setValue(BlockPart.PROPERTY, BlockPart.TOP);
        if(block instanceof FreezerBlock freezer)
            return freezer.getFridgeBlock().defaultBlockState().setValue(FreezerBlock.DIRECTION, player.getDirection());
        return null;
    }

    private void drawBlock(VertexConsumer consumer, PoseStack.Pose pose, BlockState state, VoxelShape shape, BlockPos pos, Vec3 camera)
    {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        ModelEdges cached = this.edgesCache.get(state);
        if(cached == null || cached.model != model)
        {
            cached = new ModelEdges(model, getModelEdges(state, model));
            this.edgesCache.put(state, cached);
        }
        if(cached.edges.length == 0)
        {
            drawShape(consumer, pose, shape, pos, camera);
            return;
        }

        double offsetX = pos.getX() - camera.x;
        double offsetY = pos.getY() - camera.y;
        double offsetZ = pos.getZ() - camera.z;
        float[] edges = cached.edges;
        for(int i = 0; i < edges.length; i += 6)
        {
            drawLine(consumer, pose, edges[i] + offsetX, edges[i + 1] + offsetY, edges[i + 2] + offsetZ, edges[i + 3] + offsetX, edges[i + 4] + offsetY, edges[i + 5] + offsetZ);
        }
    }

    /**
     * The four sides of each face of the model, which also covers the rotated parts. A side shared by
     * two faces is only kept once. Returned as x1, y1, z1, x2, y2, z2 in a flat array.
     */
    private static float[] getModelEdges(BlockState state, BakedModel model)
    {
        RandomSource random = RandomSource.create(42L);
        List<BakedQuad> quads = new ArrayList<>(model.getQuads(state, null, random, ModelData.EMPTY, null));
        for(Direction direction : Direction.values())
        {
            quads.addAll(model.getQuads(state, direction, random, ModelData.EMPTY, null));
        }

        Set<String> seen = new HashSet<>();
        List<float[]> found = new ArrayList<>();
        for(BakedQuad quad : quads)
        {
            int[] data = quad.getVertices();
            int stride = data.length / 4;
            for(int i = 0; i < 4; i++)
            {
                float[] start = readPosition(data, i * stride);
                float[] end = readPosition(data, ((i + 1) % 4) * stride);
                String startKey = positionKey(start);
                String endKey = positionKey(end);
                if(startKey.equals(endKey))
                    continue;
                if(seen.add(startKey.compareTo(endKey) < 0 ? startKey + endKey : endKey + startKey))
                {
                    found.add(new float[]{start[0], start[1], start[2], end[0], end[1], end[2]});
                }
            }
        }

        float[] edges = new float[found.size() * 6];
        for(int i = 0; i < found.size(); i++)
        {
            System.arraycopy(found.get(i), 0, edges, i * 6, 6);
        }
        return edges;
    }

    private static float[] readPosition(int[] data, int offset)
    {
        return new float[]{Float.intBitsToFloat(data[offset]), Float.intBitsToFloat(data[offset + 1]), Float.intBitsToFloat(data[offset + 2])};
    }

    private static String positionKey(float[] position)
    {
        return Math.round(position[0] * 1000F) + "," + Math.round(position[1] * 1000F) + "," + Math.round(position[2] * 1000F) + ";";
    }

    private record ModelEdges(BakedModel model, float[] edges) {}

    private static void drawShape(VertexConsumer consumer, PoseStack.Pose pose, VoxelShape shape, BlockPos pos, Vec3 camera)
    {
        double offsetX = pos.getX() - camera.x;
        double offsetY = pos.getY() - camera.y;
        double offsetZ = pos.getZ() - camera.z;
        shape.forAllEdges((x1, y1, z1, x2, y2, z2) -> drawLine(consumer, pose, x1 + offsetX, y1 + offsetY, z1 + offsetZ, x2 + offsetX, y2 + offsetY, z2 + offsetZ));
    }

    private static void drawLine(VertexConsumer consumer, PoseStack.Pose pose, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        float normalX = (float) (x2 - x1);
        float normalY = (float) (y2 - y1);
        float normalZ = (float) (z2 - z1);
        float length = Mth.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        normalX /= length;
        normalY /= length;
        normalZ /= length;
        consumer.vertex(pose.pose(), (float) x1, (float) y1, (float) z1).color(1.0F, 1.0F, 1.0F, ALPHA).normal(pose.normal(), normalX, normalY, normalZ).endVertex();
        consumer.vertex(pose.pose(), (float) x2, (float) y2, (float) z2).color(1.0F, 1.0F, 1.0F, ALPHA).normal(pose.normal(), normalX, normalY, normalZ).endVertex();
    }
}
