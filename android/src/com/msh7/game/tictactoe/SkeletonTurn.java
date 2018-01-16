/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.msh7.game.tictactoe;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Basic turn data. It's just a blank data string and a turn number counter.
 *
 * @author wolff
 */
public class SkeletonTurn {

    public static final String TAG = "EBTurn";

    public String message = "";
    public int row;
    public int col;

    public SkeletonTurn() {
    }

    // This is the byte array we will write out to the TBMP API.
    byte[] persist() {
        JSONObject retVal = new JSONObject();

        try {
            retVal.put("row", row);
            retVal.put("col", col);
            retVal.put("message", message);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String st = retVal.toString();
        Log.d(TAG, "==== PERSISTING\n" + st);
        return st.getBytes(Charset.forName("UTF-8"));
    }

    // Creates a new instance of SkeletonTurn.
    static SkeletonTurn unpersist(byte[] byteArray) {

        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new SkeletonTurn();
        }

        String st;
        try {
            st = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }

        Log.d(TAG, "====UNPERSIST \n" + st);
        SkeletonTurn retVal = new SkeletonTurn();
        try {
            JSONObject obj = new JSONObject(st);
            if (obj.has("row")) {
                retVal.row = obj.getInt("row");
            }
            if (obj.has("col")) {
                retVal.col = obj.getInt("col");
            }
            if (obj.has("message")) {
                retVal.message = obj.getString("message");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return retVal;
    }
}