/*
 * _=_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_=
 * TheScheideggers.com
 * _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
 * Copyright (C) 2016 - 2016 William Scheidegger
 * _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
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
 * =_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_=_
 */
package com.thescheideggers.xjcpluginexample;

import com.sun.codemodel.*;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.io.IOException;

public class XJCPluginExample extends Plugin {

    public static final String VARNAME = "-Xexample-plugin-varname";
    public static final String ARG_GETTER = "-Xexample-plugin-getter";
    public static final String ARG_SETTER = "-Xexample-plugin-setter";
    public static final String EXAMPLE_VARNAME = "id";
    public static final JType LONG_TYPE = new JCodeModel().LONG;
    public static final String EXAMPLE_GETTER = "getId";
    public static final JType VOID_TYPE = new JCodeModel().VOID;
    public static final String EXAMPLE_SETTER = "setId";
    private String varname = EXAMPLE_VARNAME;
    private String getter = EXAMPLE_GETTER;
    private String setter = EXAMPLE_SETTER;

    @Override
    public String getOptionName() {
        return "Xexample-plugin";
    }

    @Override
    public String getUsage() {
        return "  -Xexample-plugin    :  xjc example plugin";
    }

    @Override
    public int parseArgument(Options opt, String[] args, int i) throws BadCommandLineException, IOException {
        int rtn = 0;
        boolean debug = opt.debugMode;
        if (debug) {
            System.err.println("args[" + i + "] = " + args[i]);
        }
        if (args.length >= i + 2) {
            if (debug) {
                System.err.println("args[" + (i + 1) + "] = " + args[i + 1]);
            }
            if (args[i].equals(VARNAME)) {
                varname = args[i + 1];
                rtn = 2;
            } else if (args[i].equals(ARG_GETTER)) {
                getter = args[i + 1];
                rtn = 2;
            } else if (args[i].equals(ARG_SETTER)) {
                setter = args[i + 1];
                rtn = 2;
            }
        }
        return rtn;
    }

    @Override
    public boolean run(Outline model, Options opt, ErrorHandler errorHandler) throws SAXException {
        boolean debug = opt.debugMode;
        if (debug) {
            System.err.println("name   = " + varname);
            System.err.println("getter = " + getter);
            System.err.println("setter = " + setter);
        }

        for (ClassOutline classOutline : model.getClasses()) {
            JFieldVar globalId = classOutline.implClass.field(JMod.PRIVATE, LONG_TYPE, varname);

            JMethod idGetterMethod = classOutline.implClass.method(JMod.PUBLIC, LONG_TYPE, getter);
            JDocComment idGetterComment = idGetterMethod.javadoc();
            idGetterComment.add("The accessor for " + varname + ".");
            idGetterComment.addReturn().add("Returns the value of " + varname + ".");
            JBlock idGetterBlock = idGetterMethod.body();
            idGetterBlock._return(globalId);

            JMethod idSetterMethod = classOutline.implClass.method(JMod.PUBLIC, VOID_TYPE, setter);
            JDocComment idSetterComment = idSetterMethod.javadoc();
            idSetterComment.add("The mutator for " + varname + ".");
            idSetterComment.addParam("_" + varname).add("The new value to set " + varname + " to.");
            JVar localId = idSetterMethod.param(LONG_TYPE, "_" + varname);
            JBlock idSetterBlock = idSetterMethod.body();
            idSetterBlock.assign(globalId, localId);
        }
        return true;
    }
}
