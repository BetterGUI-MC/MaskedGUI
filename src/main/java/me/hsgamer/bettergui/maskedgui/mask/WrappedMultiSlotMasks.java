package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.maskedgui.api.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.maskedgui.util.MultiSlotUtil;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.mask.impl.MultiSlotsMask;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WrappedMultiSlotMasks extends BaseWrappedMask<MultiSlotsMask> {
    public WrappedMultiSlotMasks(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected MultiSlotsMask createMask(Map<String, Object> section) {
        List<Integer> slots = MultiSlotUtil.getSlots(section);
        MultiSlotsMask mask = new MultiSlotsMask(getName(), slots);
        MapUtil.castOptionalStringObjectMap(MapUtil.getIfFound(section, "button", "buttons")).ifPresent(map -> {
            List<Button> buttons = MaskUtil.createButtons(this, map);
            mask.addButtons(buttons);
        });
        return mask;
    }

    @Override
    public void refresh(UUID uuid) {
        mask.getButtons().stream()
                .filter(WrappedButton.class::isInstance)
                .map(WrappedButton.class::cast)
                .forEach(wrappedButton -> wrappedButton.refresh(uuid));
    }
}
