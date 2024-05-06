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
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_airplane
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_airport
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_amphitheatre
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_apartments
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_aqueduct
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_archive
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_atom
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_balcony
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_ballroom
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_bank
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_bank_front
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_barley
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_barn
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_baseball
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_basement
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_basketball_hoop
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_beach
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_bed
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_beer
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_billiards_rack
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_book_open
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_bowling
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_boxing_glove
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_bridge
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_broom
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_bus
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_bus_stop
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_cactus
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_candycane
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_car
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_car_wrench
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_castle
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_cave
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_chair_rolling
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_chess_rook
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_church
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_coffee
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_corn
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_countertop
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_cow
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_cowboy
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_cradle
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_crosswalk
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_cupcake
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_dam
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_department
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_desktop_classic
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_diamond
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_door
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_dumbbell
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_dump_truck
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_elevator
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_escalator
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_exit_run
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_factory
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_fence
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_ferry
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_fire_hydrant
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_fish
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_fishbowl
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_flower
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_food
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_forest
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_fountain
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_garage
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_gas_station
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_gavel
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_gift
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_golf
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_grapes
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_grass
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_grave_stone
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_greenhouse
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_hangar
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_hanger
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_hard_hat
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_helipad
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_highway
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_hockey_puck
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_home_group
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_horse
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_hospital
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_hot_tub
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_ice_cream
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_iceberg
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_igloo
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_island
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_karate
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_knife
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_library
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_lifebuoy
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_lighthouse
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_locker
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_mosque
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_mountains
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_office_building
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_oil_pump
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_pagoda
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_palace
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_palette
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_parking
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_patio_heater
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_paw
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_person_board
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_pharmacy
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_pier
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_pier_crane
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_pills
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_pizza
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_pool
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_pool_warm
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_prisoner
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_raft
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_reception
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_rice
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_road
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_robot_industrial
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_ruins
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_sail_boat
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_scalpel
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_school
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_seat
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_seesaw
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_server
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_shoe
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_shopping
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_shovel
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_shower
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_silverware
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_skate
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_ski
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_slide
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_snowflake
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_soccer_field
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_sofa
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_stadium_outline
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_stairs
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_store
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_subway
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_synagogue
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_table_picnic
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_teddy_bear
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_television
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_temple_buddhist
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_tent
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_test_tube
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_theater
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_ticket
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_tools
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_toy_brick
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_train
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_tunnel
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_violin
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_volcano
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_volleyball
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_wall_phone
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_wardrobe
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_washing_machine
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_waterfall
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_waves
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_weather_sunset
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_wharf
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_wind_turbine
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_windmill
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images.ic_wine
import dev.icerock.moko.resources.ImageResource

object LightboxCaptionIcons {

    @Composable
    fun icon(caption: String): ImageResource? = remember(caption) {
        icons[caption]
    }

    val icons = mapOf(
        "airfield" to ic_airport,
        "airplane cabin" to ic_airplane,
        "airport terminal" to ic_airport,
        "alcove" to ic_tunnel,
        "alley" to null,
        "amphitheater" to ic_amphitheatre,
        "amusement arcade" to null,
        "amusement park" to null,
        "apartment building outdoor" to ic_apartments,
        "aquarium" to ic_fishbowl,
        "aqueduct" to ic_aqueduct,
        "arcade" to null,
        "arch" to ic_tunnel,
        "archaelogical excavation" to ic_shovel,
        "archive" to ic_archive,
        "arena hockey" to ic_hockey_puck,
        "arena performance" to ic_stadium_outline,
        "arena rodeo" to ic_cowboy,
        "army base" to null,
        "art gallery" to ic_palette,
        "art school" to ic_palette,
        "art studio" to ic_palette,
        "artists loft" to ic_palette,
        "assembly line" to ic_robot_industrial,
        "athletic field outdoor" to ic_soccer_field,
        "atrium public" to null,
        "attic" to null,
        "auditorium" to ic_seat,
        "auto factory" to ic_robot_industrial,
        "auto showroom" to ic_car,
        "badlands" to ic_weather_sunset,
        "bakery shop" to ic_cupcake,
        "balcony exterior" to ic_balcony,
        "balcony interior" to ic_balcony,
        "ball pit" to null,
        "ballroom" to ic_ballroom,
        "bamboo forest" to ic_forest,
        "bank vault" to ic_bank,
        "banquet hall" to ic_ballroom,
        "bar" to ic_wine,
        "barndoor" to ic_barn,
        "baseball field" to ic_baseball,
        "basement" to ic_basement,
        "basketball court indoor" to ic_basketball_hoop,
        "bathroom" to ic_shower,
        "bazaar indoor" to ic_shopping,
        "bazaar outdoor" to ic_shopping,
        "beach" to ic_beach,
        "beach house" to null,
        "beauty salon" to null,
        "bedchamber" to ic_bed,
        "bedroom" to ic_bed,
        "beer garden" to ic_beer,
        "beer hall" to ic_beer,
        "berth" to ic_lifebuoy,
        "biology laboratory" to ic_test_tube,
        "boardwalk" to null,
        "boat deck" to ic_ferry,
        "boathouse" to ic_ferry,
        "bookstore" to ic_book_open,
        "booth indoor" to null,
        "botanical garden" to ic_flower,
        "bow window indoor" to null,
        "bowling alley" to ic_bowling,
        "boxing ring" to ic_boxing_glove,
        "bridge" to ic_bridge,
        "bullring" to ic_cow,
        "burial chamber" to ic_grave_stone,
        "bus interior" to ic_bus,
        "bus station indoor" to ic_bus_stop,
        "butchers shop" to ic_knife,
        "butte" to ic_mountains,
        "cabin outdoor" to null,
        "cafeteria" to ic_coffee,
        "campsite" to ic_tent,
        "campus" to null,
        "canal natural" to null,
        "canal urban" to null,
        "candy store" to ic_candycane,
        "canyon" to ic_mountains,
        "car interior" to ic_car,
        "carrousel" to null,
        "castle" to ic_castle,
        "catacomb" to ic_grave_stone,
        "cemetery" to ic_grave_stone,
        "chalet" to ic_castle,
        "chemistry lab" to ic_test_tube,
        "childs room" to ic_teddy_bear,
        "church indoor" to ic_church,
        "church outdoor" to ic_church,
        "classroom" to ic_person_board,
        "clean room" to null,
        "cliff" to null,
        "closet" to ic_wardrobe,
        "clothing store" to ic_hanger,
        "coast" to null,
        "cockpit" to null,
        "coffee shop" to ic_coffee,
        "computer room" to ic_desktop_classic,
        "conference center" to ic_person_board,
        "conference room" to ic_person_board,
        "construction site" to ic_hard_hat,
        "corn field" to ic_corn,
        "corral" to null,
        "corridor" to null,
        "cottage" to null,
        "courthouse" to ic_gavel,
        "courtyard" to null,
        "creek" to null,
        "crevasse" to null,
        "crosswalk" to ic_crosswalk,
        "dam" to ic_dam,
        "delicatessen" to null,
        "department store" to ic_department,
        "desert road" to null,
        "desert sand" to null,
        "desert vegetation" to ic_cactus,
        "diner outdoor" to null,
        "dining hall" to null,
        "dining room" to null,
        "discotheque" to null,
        "doorway outdoor" to ic_door,
        "dorm room" to null,
        "downtown" to null,
        "dressing room" to ic_hanger,
        "driveway" to ic_road,
        "drugstore" to ic_pills,
        "elevator door" to ic_elevator,
        "elevator lobby" to ic_elevator,
        "elevator shaft" to ic_elevator,
        "embassy" to null,
        "engine room" to null,
        "entrance hall" to null,
        "escalator indoor" to ic_escalator,
        "excavation" to null,
        "fabric store" to null,
        "farm" to ic_barn,
        "fastfood restaurant" to ic_food,
        "field cultivated" to null,
        "field road" to null,
        "field wild" to null,
        "fire escape" to ic_exit_run,
        "fire station" to ic_fire_hydrant,
        "fishpond" to ic_fish,
        "flea market indoor" to null,
        "florist shop indoor" to ic_flower,
        "food court" to ic_food,
        "football field" to ic_soccer_field,
        "forest broadleaf" to ic_forest,
        "forest path" to ic_forest,
        "forest road" to ic_forest,
        "formal garden" to null,
        "fountain" to ic_fountain,
        "galley" to ic_sail_boat,
        "garage indoor" to ic_garage,
        "garage outdoor" to ic_garage,
        "gas station" to ic_gas_station,
        "gazebo exterior" to ic_greenhouse,
        "general store indoor" to ic_store,
        "general store outdoor" to ic_store,
        "gift shop" to ic_gift,
        "glacier" to ic_iceberg,
        "golf course" to ic_golf,
        "greenhouse indoor" to ic_greenhouse,
        "greenhouse outdoor" to ic_greenhouse,
        "grotto" to ic_cave,
        "gymnasium indoor" to ic_dumbbell,
        "hangar indoor" to ic_hangar,
        "hangar outdoor" to ic_hangar,
        "harbor" to ic_wharf,
        "hardware store" to ic_tools,
        "hayfield" to null,
        "heliport" to ic_helipad,
        "highway" to ic_highway,
        "home office" to ic_chair_rolling,
        "home theater" to ic_theater,
        "hospital" to ic_hospital,
        "hospital room" to ic_hospital,
        "hot spring" to null,
        "hotel outdoor" to ic_bed,
        "hotel room" to ic_bed,
        "hunting lodge outdoor" to null,
        "ice cream parlor" to ic_ice_cream,
        "ice floe" to ic_iceberg,
        "ice shelf" to ic_iceberg,
        "ice skating rink indoor" to ic_skate,
        "ice skating rink outdoor" to ic_skate,
        "iceberg" to ic_iceberg,
        "igloo" to ic_igloo,
        "indoor" to null,
        "industrial area" to ic_factory,
        "inn outdoor" to null,
        "islet" to ic_island,
        "jacuzzi indoor" to ic_hot_tub,
        "jail cell" to ic_prisoner,
        "japanese garden" to null,
        "jewelry shop" to ic_diamond,
        "junkyard" to ic_dump_truck,
        "kasbah" to ic_castle,
        "kennel outdoor" to null,
        "kindergarden classroom" to ic_teddy_bear,
        "kitchen" to ic_countertop,
        "lagoon" to null,
        "lake natural" to null,
        "landfill" to ic_dump_truck,
        "landing deck" to null,
        "laundromat" to ic_washing_machine,
        "lawn" to ic_grass,
        "lecture room" to ic_person_board,
        "legislative chamber" to ic_gavel,
        "library indoor" to ic_library,
        "library outdoor" to ic_library,
        "lighthouse" to ic_lighthouse,
        "living room" to ic_sofa,
        "loading dock" to ic_pier_crane,
        "lobby" to null,
        "lock chamber" to null,
        "locker room" to ic_locker,
        "mansion" to null,
        "market indoor" to ic_shopping,
        "market outdoor" to ic_shopping,
        "marsh" to null,
        "martial arts gym" to ic_karate,
        "mausoleum" to ic_grave_stone,
        "medina" to null,
        "mezzanine" to null,
        "moat water" to null,
        "mosque outdoor" to ic_mosque,
        "motel" to ic_bed,
        "mountain" to ic_mountains,
        "mountain path" to ic_mountains,
        "mountain snowy" to ic_mountains,
        "movie theater indoor" to ic_theater,
        "museum indoor" to ic_bank_front,
        "museum outdoor" to ic_bank_front,
        "music studio" to null,
        "natural history museum" to ic_bank_front,
        "nursery" to ic_cradle,
        "nursing home" to null,
        "oast house" to null,
        "ocean" to ic_waves,
        "office" to ic_office_building,
        "office building" to ic_office_building,
        "office cubicles" to ic_chair_rolling,
        "oilrig" to ic_oil_pump,
        "operating room" to ic_scalpel,
        "orchard" to null,
        "orchestra pit" to ic_violin,
        "outdoor" to null,
        "pagoda" to ic_pagoda,
        "palace" to ic_palace,
        "pantry" to null,
        "park" to ic_seesaw,
        "parking garage indoor" to ic_garage,
        "parking garage outdoor" to ic_garage,
        "parking lot" to ic_parking,
        "pasture" to ic_grass,
        "patio" to ic_patio_heater,
        "pavilion" to null,
        "pet shop" to ic_paw,
        "pharmacy" to ic_pharmacy,
        "phone booth" to ic_wall_phone,
        "physics laboratory" to ic_atom,
        "picnic area" to ic_table_picnic,
        "pier" to ic_pier,
        "pizzeria" to ic_pizza,
        "playground" to ic_seesaw,
        "playroom" to ic_toy_brick,
        "plaza" to null,
        "porch" to null,
        "promenade" to null,
        "pub indoor" to ic_beer,
        "racecourse" to ic_horse,
        "raceway" to ic_horse,
        "raft" to ic_raft,
        "railroad track" to ic_fence,
        "rainforest" to ic_forest,
        "reception" to ic_reception,
        "recreation room" to ic_billiards_rack,
        "repair shop" to ic_car_wrench,
        "residential neighborhood" to null,
        "restaurant" to ic_silverware,
        "restaurant kitchen" to ic_countertop,
        "restaurant patio" to null,
        "rice paddy" to ic_rice,
        "rock arch" to ic_tunnel,
        "roof garden" to null,
        "rope bridge" to ic_bridge,
        "ruin" to ic_ruins,
        "runway" to null,
        "sandbox" to null,
        "sauna" to ic_pool_warm,
        "schoolhouse" to ic_school,
        "science museum" to ic_atom,
        "server room" to ic_server,
        "shed" to ic_greenhouse,
        "shoe shop" to ic_shoe,
        "shopfront" to ic_shopping,
        "shopping mall indoor" to ic_shopping,
        "shower" to ic_shower,
        "ski resort" to ic_ski,
        "ski slope" to ic_ski,
        "sky" to null,
        "skyscraper" to ic_apartments,
        "slum" to null,
        "snowfield" to ic_snowflake,
        "soccer field" to ic_soccer_field,
        "stable" to ic_horse,
        "stadium baseball" to ic_baseball,
        "stadium football" to ic_soccer_field,
        "stadium soccer" to ic_soccer_field,
        "stage indoor" to null,
        "stage outdoor" to null,
        "staircase" to ic_stairs,
        "storage room" to null,
        "street" to ic_road,
        "subway station platform" to ic_subway,
        "supermarket" to ic_shopping,
        "sushi bar" to null,
        "swamp" to null,
        "swimming hole" to ic_pool,
        "swimming pool indoor" to ic_pool,
        "swimming pool outdoor" to ic_pool,
        "synagogue outdoor" to ic_synagogue,
        "television room" to ic_television,
        "television studio" to ic_television,
        "temple asia" to ic_temple_buddhist,
        "throne room" to null,
        "ticket booth" to ic_ticket,
        "topiary garden" to null,
        "tower" to ic_chess_rook,
        "toyshop" to ic_toy_brick,
        "train interior" to ic_train,
        "train station platform" to ic_train,
        "tree farm" to ic_forest,
        "tree house" to null,
        "trench" to null,
        "tundra" to null,
        "underwater ocean deep" to null,
        "utility room" to ic_broom,
        "vegetable garden" to null,
        "veterinarians office" to ic_paw,
        "viaduct" to ic_aqueduct,
        "village" to ic_home_group,
        "vineyard" to ic_grapes,
        "volcano" to ic_volcano,
        "volleyball court outdoor" to ic_volleyball,
        "waiting room" to null,
        "water park" to ic_slide,
        "water tower" to null,
        "waterfall" to ic_waterfall,
        "watering hole" to null,
        "wave" to ic_waves,
        "wet bar" to ic_beer,
        "wheat field" to ic_barley,
        "wind farm" to ic_wind_turbine,
        "windmill" to ic_windmill,
        "yard" to null,
        "youth hostel" to null,
        "zen garden" to null,
    )
}