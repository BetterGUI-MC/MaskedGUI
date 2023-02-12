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
package me.hsgamer.bettergui.maskedgui.api.signal;

import me.hsgamer.bettergui.api.menu.Menu;

public class BaseSignal implements Signal {
    private final String name;
    private final Menu menu;

    public BaseSignal(String name, Menu menu) {
        this.name = name;
        this.menu = menu;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Menu getMenu() {
        return menu;
    }
}
