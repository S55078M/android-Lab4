package com.lab4_netty.lab4.utils;


import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class JSEvaluator {

    public static String eval(String functionNameInJavaScriptCode,
                              String javaScriptCode,
                              Object[] params) {

        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        try {
            Scriptable scope = rhino.initStandardObjects();

            rhino.evaluateString(scope, javaScriptCode, "JavaScript", 1, null);

            Object obj = scope.get(functionNameInJavaScriptCode, scope);

            if (obj instanceof Function) {
                Function jsFunction = (Function) obj;

                Object jsResult = jsFunction.call(rhino, scope, scope, params);

                return Context.toString(jsResult);
            }
        } finally {
            Context.exit();
        }

        return null;

    }

}
