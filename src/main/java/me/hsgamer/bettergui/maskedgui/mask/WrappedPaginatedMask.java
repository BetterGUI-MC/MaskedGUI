package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.signal.ChangePageSignal;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.bukkit.gui.mask.impl.PaginatedMask;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public abstract class WrappedPaginatedMask<T extends PaginatedMask> extends BaseWrappedMask<T> {
    private String signalId = "";

    protected WrappedPaginatedMask(MaskBuilder.Input input) {
        super(input);
        input.menu.getVariableManager().register(getName() + "_page", (original, uuid) -> {
            if (mask == null) return null;
            return Integer.toString(mask.getPage(uuid));
        });
    }

    protected abstract T createPaginatedMask(Map<String, Object> section);

    @Override
    protected T createMask(Map<String, Object> section) {
        T mask = createPaginatedMask(section);
        Optional.ofNullable(section.get("cycle")).map(Object::toString).map(Boolean::parseBoolean).ifPresent(mask::setCycle);
        signalId = Objects.toString(MapUtil.getIfFoundOrDefault(section, getName(), "signal", "signal-id"));
        return mask;
    }

    @Override
    public void handleSignal(UUID uuid, Signal signal) {
        if (signal instanceof ChangePageSignal) {
            ChangePageSignal changePageSignal = (ChangePageSignal) signal;
            if (signalId.equals(changePageSignal.id)) {
                if (changePageSignal.next) {
                    mask.nextPage(uuid);
                } else {
                    mask.previousPage(uuid);
                }
            }
        }
    }
}
