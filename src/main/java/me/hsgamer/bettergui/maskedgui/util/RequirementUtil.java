/*
   Copyright 2023-2024 Huynh Tien

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
package me.hsgamer.bettergui.maskedgui.util;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.hscore.task.BatchRunnable;

import java.util.UUID;

public final class RequirementUtil {
    private RequirementUtil() {
        // EMPTY
    }

    public static boolean check(UUID uuid, RequirementApplier requirementApplier) {
        Requirement.Result result = requirementApplier.getResult(uuid);

        BatchRunnable batchRunnable = new BatchRunnable();
        batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
            result.applier.accept(uuid, process);
            process.next();
        });
        AsyncScheduler.get(BetterGUI.getInstance()).run(batchRunnable);

        return result.isSuccess;
    }
}
