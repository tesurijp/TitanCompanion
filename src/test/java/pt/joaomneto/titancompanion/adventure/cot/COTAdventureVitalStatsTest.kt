package pt.joaomneto.titancompanion.adventure.cot

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import pt.joaomneto.titancompanion.adventure.AdventureVitalStatsTest
import pt.joaomneto.titancompanion.adventure.impl.COTAdventure
import pt.joaomneto.titancompanion.adventure.impl.fragments.AdventureVitalStatsFragment
import pt.joaomneto.titancompanion.consts.FightingFantasyGamebook

@RunWith(RobolectricTestRunner::class)
class COTAdventureVitalStatsTest :
    AdventureVitalStatsTest<COTAdventure, AdventureVitalStatsFragment>(
        COTAdventure::class,
        AdventureVitalStatsFragment::class,
        SAVEGAME
    ) {

    companion object {
        val SAVEGAME =
            mapOf(
                "gamebook" to FightingFantasyGamebook.CITY_OF_THIEVES.name,
                "name" to "test",
                "initialSkill" to "12",
                "initialLuck" to "12",
                "initialStamina" to "24",
                "currentSkill" to "12",
                "currentLuck" to "12",
                "currentStamina" to "24",
                "currentReference" to "1",
                "equipment" to " ",
                "notes" to " ",
                "gold" to "10",
                "standardPotion" to "1",
                "standardPotionValue" to "2",
                "provisions" to "10",
                "provisionsValue" to "4"
            ).toProperties()
    }
}
