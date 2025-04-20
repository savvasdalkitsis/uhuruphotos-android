/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.DrawableResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_airplane
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_airport
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_amphitheatre
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_apartments
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_aqueduct
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_archive
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_atom
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_balcony
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_ballroom
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_bank
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_bank_front
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_barley
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_barn
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_baseball
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_basement
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_basketball_hoop
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_beach
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_bed
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_beer
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_billiards_rack
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_book_open
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_bowling
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_boxing_glove
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_bridge
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_broom
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_bus
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_bus_stop
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cactus
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_candycane
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_car
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_car_wrench
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_castle
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cave
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_chair_rolling
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_chess_rook
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_church
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_coffee
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_corn
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_countertop
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cow
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cowboy
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cradle
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_crosswalk
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cupcake
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_dam
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_department
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_desktop_classic
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_diamond
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_door
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_dumbbell
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_dump_truck
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_elevator
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_escalator
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_exit_run
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_factory
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_fence
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_ferry
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_fire_hydrant
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_fish
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_fishbowl
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_flower
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_food
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_forest
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_fountain
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_garage
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_gas_station
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_gavel
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_gift
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_golf
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_grapes
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_grass
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_grave_stone
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_greenhouse
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_hangar
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_hanger
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_hard_hat
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_helipad
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_highway
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_hockey_puck
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_home_group
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_horse
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_hospital
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_hot_tub
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_ice_cream
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_iceberg
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_igloo
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_island
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_karate
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_knife
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_library
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_lifebuoy
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_lighthouse
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_locker
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_mosque
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_mountains
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_office_building
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_oil_pump
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_pagoda
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_palace
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_palette
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_parking
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_patio_heater
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_paw
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_person_board
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_pharmacy
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_pier
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_pier_crane
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_pills
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_pizza
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_pool
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_pool_warm
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_prisoner
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_raft
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_reception
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_rice
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_road
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_robot_industrial
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_ruins
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_sail_boat
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_scalpel
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_school
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_seat
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_seesaw
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_server
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_shoe
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_shopping
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_shovel
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_shower
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_silverware
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_skate
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_ski
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_slide
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_snowflake
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_soccer_field
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_sofa
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_stadium_outline
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_stairs
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_store
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_subway
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_synagogue
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_table_picnic
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_teddy_bear
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_television
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_temple_buddhist
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_tent
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_test_tube
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_theater
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_ticket
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_tools
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_toy_brick
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_train
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_tunnel
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_violin
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_volcano
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_volleyball
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_wall_phone
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_wardrobe
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_washing_machine
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_waterfall
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_waves
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_weather_sunset
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_wharf
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_wind_turbine
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_windmill
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_wine

object LightboxCaptionIcons {

    @Composable
    fun icon(caption: String): DrawableResource? = remember(caption) {
        icons[caption]
    }

    val icons = mapOf(
        "airfield" to drawable.ic_airport,
        "airplane cabin" to drawable.ic_airplane,
        "airport terminal" to drawable.ic_airport,
        "alcove" to drawable.ic_tunnel,
        "alley" to null,
        "amphitheater" to drawable.ic_amphitheatre,
        "amusement arcade" to null,
        "amusement park" to null,
        "apartment building outdoor" to drawable.ic_apartments,
        "aquarium" to drawable.ic_fishbowl,
        "aqueduct" to drawable.ic_aqueduct,
        "arcade" to null,
        "arch" to drawable.ic_tunnel,
        "archaelogical excavation" to drawable.ic_shovel,
        "archive" to drawable.ic_archive,
        "arena hockey" to drawable.ic_hockey_puck,
        "arena performance" to drawable.ic_stadium_outline,
        "arena rodeo" to drawable.ic_cowboy,
        "army base" to null,
        "art gallery" to drawable.ic_palette,
        "art school" to drawable.ic_palette,
        "art studio" to drawable.ic_palette,
        "artists loft" to drawable.ic_palette,
        "assembly line" to drawable.ic_robot_industrial,
        "athletic field outdoor" to drawable.ic_soccer_field,
        "atrium public" to null,
        "attic" to null,
        "auditorium" to drawable.ic_seat,
        "auto factory" to drawable.ic_robot_industrial,
        "auto showroom" to drawable.ic_car,
        "badlands" to drawable.ic_weather_sunset,
        "bakery shop" to drawable.ic_cupcake,
        "balcony exterior" to drawable.ic_balcony,
        "balcony interior" to drawable.ic_balcony,
        "ball pit" to null,
        "ballroom" to drawable.ic_ballroom,
        "bamboo forest" to drawable.ic_forest,
        "bank vault" to drawable.ic_bank,
        "banquet hall" to drawable.ic_ballroom,
        "bar" to drawable.ic_wine,
        "barndoor" to drawable.ic_barn,
        "baseball field" to drawable.ic_baseball,
        "basement" to drawable.ic_basement,
        "basketball court indoor" to drawable.ic_basketball_hoop,
        "bathroom" to drawable.ic_shower,
        "bazaar indoor" to drawable.ic_shopping,
        "bazaar outdoor" to drawable.ic_shopping,
        "beach" to drawable.ic_beach,
        "beach house" to null,
        "beauty salon" to null,
        "bedchamber" to drawable.ic_bed,
        "bedroom" to drawable.ic_bed,
        "beer garden" to drawable.ic_beer,
        "beer hall" to drawable.ic_beer,
        "berth" to drawable.ic_lifebuoy,
        "biology laboratory" to drawable.ic_test_tube,
        "boardwalk" to null,
        "boat deck" to drawable.ic_ferry,
        "boathouse" to drawable.ic_ferry,
        "bookstore" to drawable.ic_book_open,
        "booth indoor" to null,
        "botanical garden" to drawable.ic_flower,
        "bow window indoor" to null,
        "bowling alley" to drawable.ic_bowling,
        "boxing ring" to drawable.ic_boxing_glove,
        "bridge" to drawable.ic_bridge,
        "bullring" to drawable.ic_cow,
        "burial chamber" to drawable.ic_grave_stone,
        "bus interior" to drawable.ic_bus,
        "bus station indoor" to drawable.ic_bus_stop,
        "butchers shop" to drawable.ic_knife,
        "butte" to drawable.ic_mountains,
        "cabin outdoor" to null,
        "cafeteria" to drawable.ic_coffee,
        "campsite" to drawable.ic_tent,
        "campus" to null,
        "canal natural" to null,
        "canal urban" to null,
        "candy store" to drawable.ic_candycane,
        "canyon" to drawable.ic_mountains,
        "car interior" to drawable.ic_car,
        "carrousel" to null,
        "castle" to drawable.ic_castle,
        "catacomb" to drawable.ic_grave_stone,
        "cemetery" to drawable.ic_grave_stone,
        "chalet" to drawable.ic_castle,
        "chemistry lab" to drawable.ic_test_tube,
        "childs room" to drawable.ic_teddy_bear,
        "church indoor" to drawable.ic_church,
        "church outdoor" to drawable.ic_church,
        "classroom" to drawable.ic_person_board,
        "clean room" to null,
        "cliff" to null,
        "closet" to drawable.ic_wardrobe,
        "clothing store" to drawable.ic_hanger,
        "coast" to null,
        "cockpit" to null,
        "coffee shop" to drawable.ic_coffee,
        "computer room" to drawable.ic_desktop_classic,
        "conference center" to drawable.ic_person_board,
        "conference room" to drawable.ic_person_board,
        "construction site" to drawable.ic_hard_hat,
        "corn field" to drawable.ic_corn,
        "corral" to null,
        "corridor" to null,
        "cottage" to null,
        "courthouse" to drawable.ic_gavel,
        "courtyard" to null,
        "creek" to null,
        "crevasse" to null,
        "crosswalk" to drawable.ic_crosswalk,
        "dam" to drawable.ic_dam,
        "delicatessen" to null,
        "department store" to drawable.ic_department,
        "desert road" to null,
        "desert sand" to null,
        "desert vegetation" to drawable.ic_cactus,
        "diner outdoor" to null,
        "dining hall" to null,
        "dining room" to null,
        "discotheque" to null,
        "doorway outdoor" to drawable.ic_door,
        "dorm room" to null,
        "downtown" to null,
        "dressing room" to drawable.ic_hanger,
        "driveway" to drawable.ic_road,
        "drugstore" to drawable.ic_pills,
        "elevator door" to drawable.ic_elevator,
        "elevator lobby" to drawable.ic_elevator,
        "elevator shaft" to drawable.ic_elevator,
        "embassy" to null,
        "engine room" to null,
        "entrance hall" to null,
        "escalator indoor" to drawable.ic_escalator,
        "excavation" to null,
        "fabric store" to null,
        "farm" to drawable.ic_barn,
        "fastfood restaurant" to drawable.ic_food,
        "field cultivated" to null,
        "field road" to null,
        "field wild" to null,
        "fire escape" to drawable.ic_exit_run,
        "fire station" to drawable.ic_fire_hydrant,
        "fishpond" to drawable.ic_fish,
        "flea market indoor" to null,
        "florist shop indoor" to drawable.ic_flower,
        "food court" to drawable.ic_food,
        "football field" to drawable.ic_soccer_field,
        "forest broadleaf" to drawable.ic_forest,
        "forest path" to drawable.ic_forest,
        "forest road" to drawable.ic_forest,
        "formal garden" to null,
        "fountain" to drawable.ic_fountain,
        "galley" to drawable.ic_sail_boat,
        "garage indoor" to drawable.ic_garage,
        "garage outdoor" to drawable.ic_garage,
        "gas station" to drawable.ic_gas_station,
        "gazebo exterior" to drawable.ic_greenhouse,
        "general store indoor" to drawable.ic_store,
        "general store outdoor" to drawable.ic_store,
        "gift shop" to drawable.ic_gift,
        "glacier" to drawable.ic_iceberg,
        "golf course" to drawable.ic_golf,
        "greenhouse indoor" to drawable.ic_greenhouse,
        "greenhouse outdoor" to drawable.ic_greenhouse,
        "grotto" to drawable.ic_cave,
        "gymnasium indoor" to drawable.ic_dumbbell,
        "hangar indoor" to drawable.ic_hangar,
        "hangar outdoor" to drawable.ic_hangar,
        "harbor" to drawable.ic_wharf,
        "hardware store" to drawable.ic_tools,
        "hayfield" to null,
        "heliport" to drawable.ic_helipad,
        "highway" to drawable.ic_highway,
        "home office" to drawable.ic_chair_rolling,
        "home theater" to drawable.ic_theater,
        "hospital" to drawable.ic_hospital,
        "hospital room" to drawable.ic_hospital,
        "hot spring" to null,
        "hotel outdoor" to drawable.ic_bed,
        "hotel room" to drawable.ic_bed,
        "hunting lodge outdoor" to null,
        "ice cream parlor" to drawable.ic_ice_cream,
        "ice floe" to drawable.ic_iceberg,
        "ice shelf" to drawable.ic_iceberg,
        "ice skating rink indoor" to drawable.ic_skate,
        "ice skating rink outdoor" to drawable.ic_skate,
        "iceberg" to drawable.ic_iceberg,
        "igloo" to drawable.ic_igloo,
        "indoor" to null,
        "industrial area" to drawable.ic_factory,
        "inn outdoor" to null,
        "islet" to drawable.ic_island,
        "jacuzzi indoor" to drawable.ic_hot_tub,
        "jail cell" to drawable.ic_prisoner,
        "japanese garden" to null,
        "jewelry shop" to drawable.ic_diamond,
        "junkyard" to drawable.ic_dump_truck,
        "kasbah" to drawable.ic_castle,
        "kennel outdoor" to null,
        "kindergarden classroom" to drawable.ic_teddy_bear,
        "kitchen" to drawable.ic_countertop,
        "lagoon" to null,
        "lake natural" to null,
        "landfill" to drawable.ic_dump_truck,
        "landing deck" to null,
        "laundromat" to drawable.ic_washing_machine,
        "lawn" to drawable.ic_grass,
        "lecture room" to drawable.ic_person_board,
        "legislative chamber" to drawable.ic_gavel,
        "library indoor" to drawable.ic_library,
        "library outdoor" to drawable.ic_library,
        "lighthouse" to drawable.ic_lighthouse,
        "living room" to drawable.ic_sofa,
        "loading dock" to drawable.ic_pier_crane,
        "lobby" to null,
        "lock chamber" to null,
        "locker room" to drawable.ic_locker,
        "mansion" to null,
        "market indoor" to drawable.ic_shopping,
        "market outdoor" to drawable.ic_shopping,
        "marsh" to null,
        "martial arts gym" to drawable.ic_karate,
        "mausoleum" to drawable.ic_grave_stone,
        "medina" to null,
        "mezzanine" to null,
        "moat water" to null,
        "mosque outdoor" to drawable.ic_mosque,
        "motel" to drawable.ic_bed,
        "mountain" to drawable.ic_mountains,
        "mountain path" to drawable.ic_mountains,
        "mountain snowy" to drawable.ic_mountains,
        "movie theater indoor" to drawable.ic_theater,
        "museum indoor" to drawable.ic_bank_front,
        "museum outdoor" to drawable.ic_bank_front,
        "music studio" to null,
        "natural history museum" to drawable.ic_bank_front,
        "nursery" to drawable.ic_cradle,
        "nursing home" to null,
        "oast house" to null,
        "ocean" to drawable.ic_waves,
        "office" to drawable.ic_office_building,
        "office building" to drawable.ic_office_building,
        "office cubicles" to drawable.ic_chair_rolling,
        "oilrig" to drawable.ic_oil_pump,
        "operating room" to drawable.ic_scalpel,
        "orchard" to null,
        "orchestra pit" to drawable.ic_violin,
        "outdoor" to null,
        "pagoda" to drawable.ic_pagoda,
        "palace" to drawable.ic_palace,
        "pantry" to null,
        "park" to drawable.ic_seesaw,
        "parking garage indoor" to drawable.ic_garage,
        "parking garage outdoor" to drawable.ic_garage,
        "parking lot" to drawable.ic_parking,
        "pasture" to drawable.ic_grass,
        "patio" to drawable.ic_patio_heater,
        "pavilion" to null,
        "pet shop" to drawable.ic_paw,
        "pharmacy" to drawable.ic_pharmacy,
        "phone booth" to drawable.ic_wall_phone,
        "physics laboratory" to drawable.ic_atom,
        "picnic area" to drawable.ic_table_picnic,
        "pier" to drawable.ic_pier,
        "pizzeria" to drawable.ic_pizza,
        "playground" to drawable.ic_seesaw,
        "playroom" to drawable.ic_toy_brick,
        "plaza" to null,
        "porch" to null,
        "promenade" to null,
        "pub indoor" to drawable.ic_beer,
        "racecourse" to drawable.ic_horse,
        "raceway" to drawable.ic_horse,
        "raft" to drawable.ic_raft,
        "railroad track" to drawable.ic_fence,
        "rainforest" to drawable.ic_forest,
        "reception" to drawable.ic_reception,
        "recreation room" to drawable.ic_billiards_rack,
        "repair shop" to drawable.ic_car_wrench,
        "residential neighborhood" to null,
        "restaurant" to drawable.ic_silverware,
        "restaurant kitchen" to drawable.ic_countertop,
        "restaurant patio" to null,
        "rice paddy" to drawable.ic_rice,
        "rock arch" to drawable.ic_tunnel,
        "roof garden" to null,
        "rope bridge" to drawable.ic_bridge,
        "ruin" to drawable.ic_ruins,
        "runway" to null,
        "sandbox" to null,
        "sauna" to drawable.ic_pool_warm,
        "schoolhouse" to drawable.ic_school,
        "science museum" to drawable.ic_atom,
        "server room" to drawable.ic_server,
        "shed" to drawable.ic_greenhouse,
        "shoe shop" to drawable.ic_shoe,
        "shopfront" to drawable.ic_shopping,
        "shopping mall indoor" to drawable.ic_shopping,
        "shower" to drawable.ic_shower,
        "ski resort" to drawable.ic_ski,
        "ski slope" to drawable.ic_ski,
        "sky" to null,
        "skyscraper" to drawable.ic_apartments,
        "slum" to null,
        "snowfield" to drawable.ic_snowflake,
        "soccer field" to drawable.ic_soccer_field,
        "stable" to drawable.ic_horse,
        "stadium baseball" to drawable.ic_baseball,
        "stadium football" to drawable.ic_soccer_field,
        "stadium soccer" to drawable.ic_soccer_field,
        "stage indoor" to null,
        "stage outdoor" to null,
        "staircase" to drawable.ic_stairs,
        "storage room" to null,
        "street" to drawable.ic_road,
        "subway station platform" to drawable.ic_subway,
        "supermarket" to drawable.ic_shopping,
        "sushi bar" to null,
        "swamp" to null,
        "swimming hole" to drawable.ic_pool,
        "swimming pool indoor" to drawable.ic_pool,
        "swimming pool outdoor" to drawable.ic_pool,
        "synagogue outdoor" to drawable.ic_synagogue,
        "television room" to drawable.ic_television,
        "television studio" to drawable.ic_television,
        "temple asia" to drawable.ic_temple_buddhist,
        "throne room" to null,
        "ticket booth" to drawable.ic_ticket,
        "topiary garden" to null,
        "tower" to drawable.ic_chess_rook,
        "toyshop" to drawable.ic_toy_brick,
        "train interior" to drawable.ic_train,
        "train station platform" to drawable.ic_train,
        "tree farm" to drawable.ic_forest,
        "tree house" to null,
        "trench" to null,
        "tundra" to null,
        "underwater ocean deep" to null,
        "utility room" to drawable.ic_broom,
        "vegetable garden" to null,
        "veterinarians office" to drawable.ic_paw,
        "viaduct" to drawable.ic_aqueduct,
        "village" to drawable.ic_home_group,
        "vineyard" to drawable.ic_grapes,
        "volcano" to drawable.ic_volcano,
        "volleyball court outdoor" to drawable.ic_volleyball,
        "waiting room" to null,
        "water park" to drawable.ic_slide,
        "water tower" to null,
        "waterfall" to drawable.ic_waterfall,
        "watering hole" to null,
        "wave" to drawable.ic_waves,
        "wet bar" to drawable.ic_beer,
        "wheat field" to drawable.ic_barley,
        "wind farm" to drawable.ic_wind_turbine,
        "windmill" to drawable.ic_windmill,
        "yard" to null,
        "youth hostel" to null,
        "zen garden" to null,
    )
}