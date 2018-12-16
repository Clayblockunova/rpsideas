package com.kamefrede.rpsideas.spells.trick.block;

import com.kamefrede.rpsideas.blocks.RPSBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

import static com.kamefrede.rpsideas.spells.trick.block.PieceTrickConjureEtherealBlockSequence.addBlocksVal;

public class PieceTrickConjureGravityBlockSequence extends PieceTrick {

    private SpellParam position;
    private SpellParam target;
    private SpellParam maxBlocks;
    private SpellParam time;

    public PieceTrickConjureGravityBlockSequence(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false));
        addParam(target = new ParamVector(SpellParam.GENERIC_NAME_TARGET, SpellParam.GREEN, false, false));
        addParam(maxBlocks = new ParamVector(SpellParam.GENERIC_NAME_MAX, SpellParam.RED, false, true));
        addParam(time = new ParamNumber(SpellParam.GENERIC_NAME_TIME, SpellParam.PURPLE, true, false));
    }

    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        addBlocksVal(this, maxBlocks, meta);
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        Vector3 positionVal = this.<Vector3>getParamValue(context, position);
        Vector3 targetVal = this.<Vector3>getParamValue(context, target);
        Double maxBlocksVal = this.<Double>getParamValue(context, maxBlocks);
        Double timeVal = this.<Double>getParamValue(context, time);
        int maxBlocksInt = maxBlocksVal.intValue();

        if (positionVal == null)
            throw new SpellRuntimeException(SpellRuntimeException.NULL_VECTOR);

        int len = (int) targetVal.mag();
        Vector3 targetNorm = targetVal.copy().normalize();

        for (int i = 0; i < Math.min(len, maxBlocksInt); i++) {
            Vector3 blockVec = positionVal.copy().add(targetNorm.copy().multiply(i));

            if (!context.isInRadius(blockVec))
                throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);

            BlockPos pos = new BlockPos(blockVec.x, blockVec.y, blockVec.z);
            if (!context.caster.getEntityWorld().isBlockModifiable(context.caster, pos))
                continue;

            IBlockState state = context.caster.getEntityWorld().getBlockState(pos);

            if (state.getBlock() != RPSBlocks.conjuredGravityBlock) {
                PieceTrickConjureEtherealBlock.placeBlock(context.caster, context.caster.getEntityWorld(), pos, true);
                state = context.caster.getEntityWorld().getBlockState(pos);

                if (!context.caster.getEntityWorld().isRemote && state.getBlock() == RPSBlocks.conjuredGravityBlock)
                    PieceTrickConjureEtherealBlock.setColorAndTime(context, timeVal, pos, state);
            }
        }

        return null;
    }
}
