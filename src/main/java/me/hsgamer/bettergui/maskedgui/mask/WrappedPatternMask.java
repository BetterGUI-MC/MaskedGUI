package me.hsgamer.bettergui.maskedgui.mask;

import io.github.projectunified.craftux.mask.PatternMask;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.ButtonUtil;
import me.hsgamer.hscore.common.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WrappedPatternMask extends BaseWrappedMask<PatternMask> {
    public WrappedPatternMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected PatternMask createMask(Map<String, Object> section) {
        List<String> pattern = CollectionUtils.createStringListFromObject(section.get("pattern"));
        Map<String, WrappedButton> buttonElements = ButtonUtil.createChildButtons(this, section).buttonMap();

        PatternMask mask = new PatternMask(pattern);
        for (Map.Entry<String, WrappedButton> entry : buttonElements.entrySet()) {
            String key = entry.getKey();
            char c = key.isEmpty() ? ' ' : key.charAt(0);
            mask.addButton(c, new ButtonUtil.CraftUXButton(entry.getValue()));
        }
        return mask;
    }

    @Override
    protected void refresh(PatternMask mask, UUID uuid) {
        ButtonUtil.refreshCraftUXButtons(uuid, mask.getButtons().values());
    }
}
