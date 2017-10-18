package com.spongeapi.tutorial.configsexample;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.UUID;

@ConfigSerializable
public class MyEnhancedObject {
    @Setting
    private MyObject object;
    @Setting("myBestStack")
    private ItemStack stack;
    @Setting(value = "playerId", comment = "Идентификатор игрока")
    private UUID someId;

    public MyEnhancedObject() {
    }

    public MyEnhancedObject(MyObject object, ItemStack stack, UUID someId) {
        this.object = object;
        this.stack = stack;
        this.someId = someId;
    }

    public MyObject getObject() {
        return object;
    }

    public ItemStack getStack() {
        return stack;
    }

    public UUID getSomeId() {
        return someId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyEnhancedObject that = (MyEnhancedObject) o;

        if (!object.equals(that.object)) return false;
        if (!stack.equals(that.stack)) return false;
        return someId.equals(that.someId);
    }

    @Override
    public int hashCode() {
        int result = object.hashCode();
        result = 31 * result + stack.hashCode();
        result = 31 * result + someId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MyEnhancedObject{" +
                "object=" + object +
                ", stack=" + stack +
                ", someId=" + someId +
                '}';
    }
}
