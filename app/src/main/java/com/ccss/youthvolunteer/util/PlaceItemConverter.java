package com.ccss.youthvolunteer.util;

import com.ccss.youthvolunteer.model.PlaceInfo;
import com.google.common.base.Function;

public class PlaceItemConverter {
    private static class PlaceInfoListItem implements ListItem {
        private final PlaceInfo placeInfo;

        private PlaceInfoListItem(PlaceInfo placeInfo) {
            this.placeInfo = placeInfo;
        }

        @Override
        public PlaceInfo getEntry() {
            return placeInfo;
        }

        @Override
        public String getText1() {
            return placeInfo.name;
        }

        @Override
        public String getText2() {
            return placeInfo.vicinity;
        }
    }

    public static Function<PlaceInfo, ListItem> fromPlaceToListItem() {
        return new Function<PlaceInfo, ListItem>() {
            @Override
            public ListItem apply(final PlaceInfo input) {
                return new PlaceInfoListItem(input);
            }
        };
    }
}
