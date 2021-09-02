package de.macbrayne.fabric.spawnprotectiontweaks.command;

public class VanillaSyntaxGroup {
    final String root;
    final String query;
    final String set;
    final String reset;

    public VanillaSyntaxGroup(final String module, final String path) {
        this.root = module + path;
        query = new PermissionNode(module, Action.QUERY).get();
        set = new PermissionNode(module, Action.SET).get();
        reset = new PermissionNode(module, Action.RESET).get();
    }

    private record PermissionNode(String module, Action action) {
        String get() {
            return module + action.suffix;
        }
    }

    private enum Action {
        SET(".set"), QUERY(".query"), RESET(".reset");
        String suffix;

        Action(final String value) {
            this.suffix = "." + value;
        }
    }
}
