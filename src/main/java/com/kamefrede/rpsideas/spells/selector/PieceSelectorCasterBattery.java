package com.kamefrede.rpsideas.spells.selector;

import net.minecraft.item.ItemStack;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorCasterBattery extends PieceSelector {

    public PieceSelectorCasterBattery(Spell spell) {
        super(spell);
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        if (context.caster == null) return null;
        ItemStack cad = PsiAPI.getPlayerCAD(context.caster);
        if (cad != null)
            return ((ICAD) cad.getItem()).getStatValue(cad, EnumCADStat.OVERFLOW) * 1.0;
        return 0.0;
    }

    @Override
    public Class<?> getEvaluationType() {
        return Double.class;
    }
}
