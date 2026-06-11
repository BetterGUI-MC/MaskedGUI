/*
   Copyright 2023-2026 Huynh Tien

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
package me.hsgamer.bettergui.maskedgui.menu.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Position;
import io.github.projectunified.craftux.mask.HybridMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.hscore.minecraft.gui.button.ButtonMap;
import me.hsgamer.hscore.minecraft.gui.button.DisplayButton;
import me.hsgamer.hscore.minecraft.gui.object.InventoryPosition;
import me.hsgamer.hscore.minecraft.gui.object.InventorySize;
import me.hsgamer.hscore.minecraft.gui.object.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class CraftUXButtonMap implements ButtonMap {
    private final HybridMask hybridMask = new HybridMask();

    @Override
    public @NotNull Map<@NotNull Integer, @NotNull DisplayButton> getButtons(@NotNull UUID uuid, InventorySize inventorySize) {
        Map<Position, ActionItem> map = hybridMask.getActionMap(uuid);
        if (map == null) return Collections.emptyMap();
        Map<Integer, @NotNull DisplayButton> buttonMap = new HashMap<>();
        for (Map.Entry<Position, ActionItem> entry : map.entrySet()) {
            Position position = entry.getKey();
            ActionItem actionItem = entry.getValue();

            int slot = InventoryPosition.of(position.getX(), position.getY()).toSlot(inventorySize);

            DisplayButton displayButton = new DisplayButton();
            Object item = actionItem.getItem();
            if (item instanceof Item) {
                displayButton.setItem((Item) item);
            }
            Consumer<Object> action = actionItem.getAction();
            if (action != null) {
                displayButton.setAction(action::accept);
            }

            buttonMap.put(slot, displayButton);
        }
        return buttonMap;
    }

    public HybridMask getHybridMask() {
        return hybridMask;
    }

    public void refresh(UUID uuid) {
        MaskUtil.refreshMasks(uuid, hybridMask.getElements());
    }

    public void handleSignal(UUID uuid, Signal signal) {
        MaskUtil.handleSignal(uuid, hybridMask.getElements(), signal);
    }
}
