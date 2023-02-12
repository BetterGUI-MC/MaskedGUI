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
package me.hsgamer.bettergui.maskedgui.action;

import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.menu.MaskedMenu;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public abstract class SignalAction extends BaseAction {
    private final Plugin plugin;

    protected SignalAction(Plugin plugin, ActionBuilder.Input input) {
        super(input);
        this.plugin = plugin;
    }

    protected abstract Signal createSignal(UUID uuid, String value);

    @Override
    public void accept(UUID uuid, TaskProcess process) {
        String value = getReplacedString(uuid);
        Menu menu = getMenu();
        if (!(menu instanceof MaskedMenu)) {
            process.next();
            return;
        }
        MaskedMenu maskedMenu = (MaskedMenu) menu;
        Signal signal = createSignal(uuid, value);
        Bukkit.getScheduler().runTask(plugin, () -> {
            maskedMenu.handleSignal(uuid, signal);
            process.next();
        });
    }
}
