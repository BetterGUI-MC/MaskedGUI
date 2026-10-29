package me.hsgamer.bettergui.maskedgui.mask;

import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.mask.ProgressMask;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.ButtonUtil;
import me.hsgamer.bettergui.maskedgui.util.MaskSlotUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class WrappedProgressMask extends BaseWrappedMask<ProgressMask> {
    public WrappedProgressMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected ProgressMask createMask(Map<String, Object> section) {
        String currentValue = Objects.toString(MapUtils.getIfFoundOrDefault(section, "0", "current-value", "current"), "0");
        String maxValue = Objects.toString(MapUtils.getIfFoundOrDefault(section, "100", "max-value", "max"), "100");

        ProgressMask mask = new ProgressMask(MaskSlotUtil.of(section, this)) {
            @Override
            protected @NotNull Value getValue(@NotNull UUID uuid) {
                String parsedCurrentValue = StringReplacerApplier.replace(currentValue, uuid, WrappedProgressMask.this);
                String parsedMaxValue = StringReplacerApplier.replace(maxValue, uuid, WrappedProgressMask.this);
                double current = Validate.getNumber(parsedCurrentValue).map(Number::doubleValue).orElse(0.0);
                double max = Validate.getNumber(parsedMaxValue).map(Number::doubleValue).orElse(100.0);
                return new Value(current, max);
            }
        };

        MapUtils.castOptionalStringObjectMap(MapUtils.getIfFound(section, "complete-button", "complete", "current-button"))
                .flatMap(map -> ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(getMenu(), getName() + "_complete_button", map)))
                .<Button>map(ButtonUtil.CraftUXButton::new)
                .ifPresent(mask::setCompleteButton);

        MapUtils.castOptionalStringObjectMap(MapUtils.getIfFound(section, "incomplete-button", "incomplete", "max-button"))
                .flatMap(map -> ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(getMenu(), getName() + "_incomplete_button", map)))
                .<Button>map(ButtonUtil.CraftUXButton::new)
                .ifPresent(mask::setIncompleteButton);

        return mask;
    }

    @Override
    protected void refresh(ProgressMask mask, UUID uuid) {
        ButtonUtil.refreshCraftUXButton(uuid, mask.getCompleteButton());
        ButtonUtil.refreshCraftUXButton(uuid, mask.getIncompleteButton());
    }
}
