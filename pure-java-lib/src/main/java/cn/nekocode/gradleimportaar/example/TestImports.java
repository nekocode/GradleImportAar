package cn.nekocode.gradleimportaar.example;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class TestImports {
    @Nullable
    private static Context context = null;

    static void test() {
        RecyclerView recyclerView = new RecyclerView(context);
    }
}
