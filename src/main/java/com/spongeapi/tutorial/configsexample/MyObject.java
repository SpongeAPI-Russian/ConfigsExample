package com.spongeapi.tutorial.configsexample;

import org.spongepowered.api.item.inventory.ItemStack;

public class MyObject {
    private final String someString;
    private final int someInt;
    private final ItemStack someStack;

    public MyObject(String someString, int someInt, ItemStack someStack) {
        this.someString = someString;
        this.someInt = someInt;
        this.someStack = someStack;
    }

    public String getSomeString() {
        return someString;
    }

    public int getSomeInt() {
        return someInt;
    }

    public ItemStack getSomeStack() {
        return someStack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyObject object = (MyObject) o;

        if (someInt != object.someInt) return false;
        if (!someString.equals(object.someString)) return false;
        return someStack.equals(object.someStack);
    }

    @Override
    public int hashCode() {
        int result = someString.hashCode();
        result = 31 * result + someInt;
        result = 31 * result + someStack.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MyObject{" +
                "someString='" + someString + '\'' +
                ", someInt=" + someInt +
                ", someStack=" + someStack +
                '}';
    }
}
