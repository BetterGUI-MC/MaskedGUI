package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.signal.ChangePageSignal;
import me.hsgamer.bettergui.maskedgui.signal.RefreshMaskSignal;
import me.hsgamer.bettergui.maskedgui.util.SignalHandler;
import me.hsgamer.hscore.minecraft.gui.mask.impl.PaginatedMask;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class WrappedPaginatedMask<T extends PaginatedMask> extends BaseWrappedMask<T> {
    protected final SignalHandler signalHandler = new SignalHandler();

    protected WrappedPaginatedMask(MaskBuilder.Input input) {
        super(input);
        input.menu.getVariableManager().register(getName() + "_page", (original, uuid) -> {
            if (getMask() == null) return null;
            return Integer.toString(getMask().getPage(uuid) + 1);
        });
        input.menu.getVariableManager().register(getName() + "_max", (original, uuid) -> {
            if (getMask() == null) return null;
            return Integer.toString(getMask().getPageAmount(uuid));
        });
    }

    protected abstract T createPaginatedMask(Map<String, Object> section);

    @Override
    protected T createMask(Map<String, Object> section) {
        T mask = createPaginatedMask(section);
        Optional.ofNullable(section.get("cycle")).map(Object::toString).map(Boolean::parseBoolean).ifPresent(mask::setCycle);
        signalHandler
                .setSignal(section, getName())
                .addHandler(ChangePageSignal.class, (uuid, changePageSignal) -> {
                    if (changePageSignal.isNext()) {
                        mask.nextPage(uuid);
                    } else {
                        mask.previousPage(uuid);
                    }
                })
                .addHandler(RefreshMaskSignal.class, (uuid, refreshMaskSignal) -> mask.setPage(uuid, 0));
        return mask;
    }

    @Override
    protected void handleSignal(T mask, UUID uuid, Signal signal) {
        signalHandler.handle(uuid, signal);
    }

    @Override
    public void stop() {
        signalHandler.clear();
        super.stop();
    }
}
