/*
   Copyright 2023-2023 Huynh Tien

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package me.hsgamer.bettergui.maskedgui.api.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Element;
import io.github.projectunified.craftux.common.Mask;
import io.github.projectunified.craftux.common.Position;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class BaseWrappedMask<T extends Mask> implements WrappedMask {
    private final MaskBuilder.Input input;
    private T mask;

    protected BaseWrappedMask(MaskBuilder.Input input) {
        this.input = input;
    }

    protected abstract T createMask(Map<String, Object> section);

    protected void handleSignal(T mask, UUID uuid, Signal signal) {
        // EMPTY
    }

    protected void refresh(T mask, UUID uuid) {
        // EMPTY
    }

    public Map<String, Object> getOptions() {
        return input.options;
    }

    public T getMask() {
        return mask;
    }

    @Override
    public Menu getMenu() {
        return input.menu;
    }

    @Override
    public String getName() {
        return input.name;
    }

    @Override
    public @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        return mask == null ? null : mask.apply(uuid);
    }

    @Override
    public void init() {
        mask = createMask(input.options);
        if (mask != null) {
            Element.handleIfElement(mask, Element::init);
        }
    }

    @Override
    public void stop() {
        if (mask != null) {
            Element.handleIfElement(mask, Element::stop);
        }
    }

    @Override
    public final void handleSignal(UUID uuid, Signal signal) {
        if (mask != null) {
            handleSignal(mask, uuid, signal);
        }
    }

    @Override
    public final void refresh(UUID uuid) {
        if (mask != null) {
            refresh(mask, uuid);
        }
    }
}
