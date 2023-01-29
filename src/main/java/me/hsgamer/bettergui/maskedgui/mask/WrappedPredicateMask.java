package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.hscore.minecraft.gui.mask.impl.PredicateMask;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class WrappedPredicateMask extends BaseWrappedMask<PredicateMask> {
    private final Set<UUID> checked = new ConcurrentSkipListSet<>();

    public WrappedPredicateMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected PredicateMask createMask(Map<String, Object> section) {
        PredicateMask predicateMask = new PredicateMask(getName());

        boolean checkOnlyOnCreation = Optional.ofNullable(section.get("check-only-on-creation")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false);
        Optional.ofNullable(MapUtil.getIfFound(section, "requirement", "view-requirement"))
                .flatMap(MapUtil::castOptionalStringObjectMap)
                .ifPresent(subsection -> {
                    RequirementApplier requirementApplier = new RequirementApplier(getMenu(), getName() + "_view", subsection);
                    predicateMask.setViewPredicate(uuid -> {
                        if (checkOnlyOnCreation && checked.contains(uuid)) {
                            return true;
                        }
                        Requirement.Result result = requirementApplier.getResult(uuid);
                        if (result.isSuccess) {
                            checked.add(uuid);
                        }
                        BetterGUI.runBatchRunnable(batchRunnable -> batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
                            result.applier.accept(uuid, process);
                            process.next();
                        }));
                        return result.isSuccess;
                    });
                });
        Optional.ofNullable(MapUtil.getIfFound(section, "success-mask", "success", "view-mask", "view"))
                .flatMap(MapUtil::castOptionalStringObjectMap)
                .flatMap(subsection -> MaskBuilder.INSTANCE.build(new MaskBuilder.Input(getMenu(), getName() + "_success", subsection)))
                .ifPresent(predicateMask::setMask);
        Optional.ofNullable(MapUtil.getIfFound(section, "fail-mask", "fail", "fallback-mask", "fallback"))
                .flatMap(MapUtil::castOptionalStringObjectMap)
                .flatMap(subsection -> MaskBuilder.INSTANCE.build(new MaskBuilder.Input(getMenu(), getName() + "_fail", subsection)))
                .ifPresent(predicateMask::setFallbackMask);
        return predicateMask;
    }

    @Override
    protected void refresh(PredicateMask mask, UUID uuid) {
        checked.remove(uuid);
        Optional.of(mask.getMask())
                .filter(WrappedMask.class::isInstance)
                .map(WrappedMask.class::cast)
                .ifPresent(wrappedMask -> wrappedMask.refresh(uuid));
        Optional.of(mask.getFallbackMask())
                .filter(WrappedMask.class::isInstance)
                .map(WrappedMask.class::cast)
                .ifPresent(wrappedMask -> wrappedMask.refresh(uuid));
    }

    @Override
    protected void handleSignal(PredicateMask mask, UUID uuid, Signal signal) {
        Optional.of(mask.getMask())
                .filter(WrappedMask.class::isInstance)
                .map(WrappedMask.class::cast)
                .ifPresent(wrappedMask -> wrappedMask.handleSignal(uuid, signal));
        Optional.of(mask.getFallbackMask())
                .filter(WrappedMask.class::isInstance)
                .map(WrappedMask.class::cast)
                .ifPresent(wrappedMask -> wrappedMask.handleSignal(uuid, signal));
    }
}
