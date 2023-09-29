package games.narcolepsy.minecraft.utils.features;

public interface Feature {
    void Enable();
    default void Disable() {};
    String getName();
}
