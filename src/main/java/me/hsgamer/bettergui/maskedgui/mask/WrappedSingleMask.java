package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.maskedgui.util.MultiSlotUtil;
import me.hsgamer.hscore.bukkit.gui.mask.impl.SingleMask;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WrappedSingleMask extends BaseWrappedMask<SingleMask> {
    public WrappedSingleMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected SingleMask createMask(Map<String, Object> section) {
        List<Integer> slots = MultiSlotUtil.getSlots(section);
        if (slots.isEmpty()) return null;
        int slot = slots.get(0);
        Optional<WrappedButton> buttonOptional = ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(getMenu(), getName() + "_button", section));
        return buttonOptional.map(wrappedButton -> new SingleMask(getName(), slot, wrappedButton)).orElse(null);
    }

    @Override
    public void refresh(UUID uuid) {
        if (mask == null) return;
        MaskUtil.refreshButtons(uuid, mask.generateButtons(uuid).values());
    }
}
