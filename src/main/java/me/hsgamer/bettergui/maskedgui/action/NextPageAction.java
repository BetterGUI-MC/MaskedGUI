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

import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.signal.NextPageSignal;

import java.util.UUID;

public class NextPageAction extends SignalAction {
    private final boolean next;

    public NextPageAction(ActionBuilder.Input input, boolean next) {
        super(input);
        this.next = next;
    }

    @Override
    protected Signal createSignal(UUID uuid, String value) {
        return new NextPageSignal(value, input.getMenu(), next);
    }
}
