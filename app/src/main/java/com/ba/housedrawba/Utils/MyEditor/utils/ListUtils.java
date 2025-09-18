package com.ba.housedrawba.Utils.MyEditor.utils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class ListUtils {


    public static int[] toIntArray(List<Integer> intList) {
        int[] result = new int[intList.size()];
        int index = 0;
        for (Integer i : intList) {
            result[index++] = i;
        }
        return result;
    }


    public static int shiftItem(List<?> list, int index, int offset) {
        if (offset == 0 || index < 0) {
            return 0;
        }
        if (offset > 0) {
            int end = index + offset + 1;
            if (end > list.size()) {
                end = list.size();
            }
            Collections.rotate(list.subList(index, end), -1);
            return end - 1;
        } else {
            int start = index + offset;
            if (start < 0) {
                start = 0;
            }
            Collections.rotate(list.subList(start, index + 1), 1);
            return start;
        }
    }


    public static void shiftItemToFront(List<?> list, int index) {
        if (index >= 0) {
            Collections.rotate(list.subList(index, list.size()), -1);
        }
    }


    public static void shiftItemToBack(List<?> list, int index) {
        if (index >= 0) {
            Collections.rotate(list.subList(0, index + 1), 1);
        }
    }


    public static void shiftItemsAsMuchAsPossible(List<?> list, int[] indexArray, int offset) {
        Arrays.sort(indexArray);
        if (offset > 0) {
            int last = list.size();
            for (int i = indexArray.length - 1; i >= 0; i--) {
                last = shiftItem(list.subList(0, last), indexArray[i], offset);
            }
        } else {
            int last = -1;
            for (int i = 0; i < indexArray.length; i++) {
                int index = indexArray[i] - (last + 1);
                last = shiftItem(list.subList(last + 1, list.size()), index, offset);
            }
        }
    }


    public static void shiftItemsWithFixedDistance(List<?> list, int[] indexArray, int offset) {
        Arrays.sort(indexArray);
        int unionOffset = computeUnionOffset(list, indexArray, offset);
        shiftItemsDirectly(list, indexArray, unionOffset);
    }


    private static void shiftItemsDirectly(List<?> list, int[] indexArray, int offset) {
        if (offset > 0) {
            for (int i = indexArray.length - 1; i >= 0; i--) {
                shiftItem(list, indexArray[i], offset);
            }
        } else {
            for (int i = 0; i < indexArray.length; i++) {
                shiftItem(list, indexArray[i], offset);
            }
        }
    }


    private static int computeUnionOffset(List<?> list, int[] indexArray, int offset) {
        if (offset > 0) {
            int unionIndex = indexArray[indexArray.length - 1];
            if (unionIndex + offset < list.size()) {
                return offset;
            } else {
                return list.size() - 1 - unionIndex;
            }
        } else {
            int unionIndex = indexArray[0];
            if (unionIndex + offset >= 0) {
                return offset;
            } else {
                return -unionIndex;
            }
        }
    }

}
