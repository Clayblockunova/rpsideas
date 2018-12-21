package com.kamefrede.rpsideas.spells.trick.misc;

import com.kamefrede.rpsideas.spells.base.SpellRuntimeExceptions;
import net.minecraft.item.ItemStack;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.common.item.ItemCAD;

public class PieceTrickSpinChamber extends PieceTrick {

    private SpellParam number;

    public PieceTrickSpinChamber(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(number = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER, SpellParam.RED, false, false));
    }

    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException, ArithmeticException {
        super.addToMetadata(meta);
        meta.addStat(EnumSpellStat.POTENCY, 1);
        meta.addStat(EnumSpellStat.COST, 5);
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        Double num = this.<Double>getParamValue(context, number);
        if (num == null || num == 0) return null;
        if (!context.tool.isEmpty()) throw new SpellRuntimeException(SpellRuntimeExceptions.CAD);
        ItemStack stack = PsiAPI.getPlayerCAD(context.caster);
        ItemCAD cad = (ItemCAD) stack.getItem();
        if (num > 0) {
            if (cad.getSelectedSlot(stack) < cad.getStatValue(stack, EnumCADStat.SOCKETS) - 1) {
                cad.setSelectedSlot(stack, cad.getSelectedSlot(stack) + 1);
            } else {
                cad.setSelectedSlot(stack, 0);
            }
        } else {
            if (cad.getSelectedSlot(stack) != 0) {
                cad.setSelectedSlot(stack, cad.getSelectedSlot(stack) - 1);
            } else {
                cad.setSelectedSlot(stack, cad.getStatValue(stack, EnumCADStat.SOCKETS) - 1);
            }
        }

        return null;
    }
}
