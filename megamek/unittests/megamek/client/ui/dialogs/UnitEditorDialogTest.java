/*
 * Copyright (C) 2026 The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MegaMek.
 *
 * MegaMek is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPL),
 * version 3 or (at your option) any later version,
 * as published by the Free Software Foundation.
 *
 * MegaMek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * A copy of the GPL should have been included with this project;
 * if not, see <https://www.gnu.org/licenses/>.
 *
 * NOTICE: The MegaMek organization is a non-profit group of volunteers
 * creating free software for the BattleTech community.
 *
 * MechWarrior, BattleMech, `Mech and AeroTech are registered trademarks
 * of The Topps Company, Inc. All Rights Reserved.
 *
 * Catalyst Game Labs and the Catalyst Game Labs logo are trademarks of
 * InMediaRes Productions, LLC.
 *
 * MechWarrior Copyright Microsoft Corporation. MegaMek was created under
 * Microsoft's "Game Content Usage Rules"
 * <https://www.xbox.com/en-US/developers/rules> and it is not endorsed by or
 * affiliated with Microsoft.
 */

package megamek.client.ui.dialogs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import megamek.common.equipment.AmmoMounted;
import megamek.common.equipment.AmmoType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link UnitEditorDialog#maxEditableShots(AmmoMounted)}, which drives the upper bound of the per-ammo-bin
 * shots spinner added to the equipment editor.
 */
@DisplayName("UnitEditorDialog ammo shots spinner range")
class UnitEditorDialogTest {

    private static AmmoMounted mockAmmo(int baseShotsLeft, int originalShots, int typeShots) {
        AmmoType ammoType = mock(AmmoType.class);
        lenient().when(ammoType.getShots()).thenReturn(typeShots);
        AmmoMounted ammo = mock(AmmoMounted.class);
        when(ammo.getBaseShotsLeft()).thenReturn(baseShotsLeft);
        when(ammo.getOriginalShots()).thenReturn(originalShots);
        lenient().when(ammo.getType()).thenReturn(ammoType);
        return ammo;
    }

    @Test
    @DisplayName("Standard bin uses the ammo type's per-ton shot count as the maximum")
    void standardBinUsesTypeShots() {
        // originalShots is 0 for normal (non by-shot) ammo, so the per-ton capacity is used
        AmmoMounted ammo = mockAmmo(10, 0, 20);
        assertEquals(20, UnitEditorDialog.maxEditableShots(ammo));
    }

    @Test
    @DisplayName("Full standard bin still allows editing up to the full capacity")
    void fullStandardBinAllowsFullCapacity() {
        AmmoMounted ammo = mockAmmo(20, 0, 20);
        assertEquals(20, UnitEditorDialog.maxEditableShots(ammo));
    }

    @Test
    @DisplayName("By-shot bin uses originalShots as the maximum")
    void byShotBinUsesOriginalShots() {
        // by-shot ammo records its full capacity in originalShots, overriding the per-ton value
        AmmoMounted ammo = mockAmmo(45, 90, 20);
        assertEquals(90, UnitEditorDialog.maxEditableShots(ammo));
    }

    @Test
    @DisplayName("Current shots act as a floor when they exceed the nominal capacity")
    void currentShotsActAsFloor() {
        AmmoMounted ammo = mockAmmo(25, 0, 20);
        assertEquals(25, UnitEditorDialog.maxEditableShots(ammo));
    }
}
