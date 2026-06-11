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

import io.github.projectunified.craftux.common.Element;
import io.github.projectunified.craftux.common.Mask;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;

import java.util.UUID;

public interface WrappedMask extends Mask, Element, MenuElement {
    @Override
    Menu getMenu();

    String getName();

    default void refresh(UUID uuid) {
        // EMPTY
    }

    default void handleSignal(UUID uuid, Signal signal) {
        // EMPTY
    }
}
