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
package me.hsgamer.bettergui.maskedgui.menu;

import me.hsgamer.bettergui.maskedgui.menu.mask.CraftUXButtonMap;
import me.hsgamer.bettergui.menu.BaseInventoryMenu;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.minecraft.gui.object.InventorySize;
import org.bukkit.event.inventory.InventoryType;

import java.util.UUID;

public class MaskedMenu extends BaseInventoryMenu<CraftUXButtonMap> implements BaseMaskedMenu {
    public MaskedMenu(Config config) {
        super(config);
    }

    @Override
    protected CraftUXButtonMap createButtonMap() {
        return BaseMaskedMenu.createButtonMap(configSettings, this);
    }

    @Override
    protected void refreshButtonMapOnCreate(CraftUXButtonMap buttonMap, UUID uuid) {
        buttonMap.refresh(uuid);
    }

    public int getSlotPerRow() {
        InventoryType inventoryType = getGUIHolder().getInventoryType();
        switch (inventoryType) {
            case CHEST:
            case ENDER_CHEST:
            case SHULKER_BOX:
                return 9;
            case DISPENSER:
            case DROPPER:
            case HOPPER:
                return 3;
            default:
                return 0;
        }
    }

    @Override
    public InventorySize makeFakeInventorySize() {
        return new InventorySize() {
            @Override
            public int getSize() {
                return 54;
            }

            @Override
            public int getSlotPerRow() {
                return MaskedMenu.this.getSlotPerRow();
            }
        };
    }
}
