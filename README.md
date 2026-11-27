# InventoryExchange

InventoryExchange is a Minecraft mod that extends player inventory management and integrates with [ProjectE](https://github.com/sinkillerj/ProjectE)’s EMC & knowledge systems to provide a creative-like experience while in survival mode.

---

## Features

- Infinite Inventory (extended inventory storage)
- Streamlined transmutation
  - Knowledge is learned when items are picked up, crafted, smelted, found in a container, and on interacting with a block.
  - Transmutation EMC is used for all other EMC needs (replacing the need of fuel items present in ProjectE).
  - Items can be transmuted using the Pick Block key similar to creative mode.
- Flying via EMC (no items required)
- Portable Crafting Table
- Portable Furnace (smelts at 1 item per tick from anywhere using EMC as fuel)
- Automated Feeding, Repairing, Smelting, and Condensing (toggleable with configurable limits)
- Ultima tool allows for vein mining.
- Ultima watch allows for time manipulation.
- Ultima armor set grants persistent effects (night vision, water breathing, fire resistance, speed) and invincibility.
- Pixelmon compatability, awarding EMC for battling and catching.

---

## Automations

Each automation has:
- Toggle keybind
- Toggle command
- Value set command
- Limited mode (set in client config to determine if it should run continuously or only until completed)

Actions:
- Feeding: Consumes EMC to restore hunger if below threshold.
- Repairing: Repairs damaged items using EMC cost per durability point at 1 durability per tick.
- Smelting: Smelts items with no EMC at 1 item per tick.
- Condensing: Converts items to EMC at 1 item per tick (excludes tools and armor).

Associated keybinds allow quick toggle while commands enable scripting or admin control.

---

## Ultima Armor Effects

Wearing the full Ultima armor set grants continuous:
- Night Vision
- Water Breathing
- Fire Resistance
- Speed (+60%)
- Invincibility
- Items gravitate towards player

---

## Keybinds

| Keybind                        | Default       | Function                                                                  |
|--------------------------------|---------------|---------------------------------------------------------------------------|
| Open Crafting                  | Numpad 1      | Opens portable crafting                                                   |
| Toggle Inventory Condensing    | Numpad 6      | Toggles condensing automation                                             |
| Toggle Feeding via EMC         | Numpad 7      | Toggles feeding automation                                                |
| Open Furnace                   | Numpad 2      | Opens portable furnace                                                    |
| Open Infinite Inventory        | Numpad 4      | Opens infinite inventory                                                  |
| Pick Block (vanilla key)       | Middle Button | Gives a stack of the targeted block using EMC (survival only, empty hand) |
| Toggle Repairing Items via EMC | Numpad 8      | Toggles repairing automation                                              |
| Toggle Smelting Items via EMC  | Numpad 3      | Toggles smelting automation                                               |
| Open Transmutation             | Numpad 5      | Opens transmutation                                                       |
| Change Ultima Tool Mode        | Numpad 9      | Cycles ultima tool mode                                                   |

Note: Pick Block uses the vanilla pick block key and is not registered as a separate keybind.

---

## Commands

Primary root aliases: `/inventoryexchange` and `/ie`

### Viewing Containers
```
/ie view furnace
/ie view furnace <player>
/ie view infiniteinventory
/ie view infiniteinventory <player>
```

### Toggle/Set Automations
```
/ie toggle <condensing|feeding|repairing|smelting>
/ie set <condensing|feeding|repairing|smelting> <true|false>
```

### EMC & Knowledge
```
/ie dumpmissingemc
/ie emc get <player> <value>
/ie emc <add|remove|set|test> <player> <value>
/ie knowledge clear <player>
/ie knowledge <learn|test|unlearn> <player> <item>
/ie removeemc <item>
/ie resetemc <item>
/ie setemc <item>
```

---

## Permissions

- `inventoryexchange.command.emc`
- `inventoryexchange.command.knowledge`
- `inventoryexchange.command.remove_emc`
- `inventoryexchange.command.reset_emc`
- `inventoryexchange.command.set_emc`
- `inventoryexchange.command.view_furnace_other`
- `inventoryexchange.command.view_infinite_inventory_other`

---

## 32x Resource Pack

For a 32x version of InventoryExchange's textures, see [InventoryExchange32x](https://github.com/eatAWESOME/InventoryExchange32x).

## Thanks

Thanks to the [Developers of ProjectE](https://github.com/sinkillerj/ProjectE?tab=readme-ov-file#current-team-members) for the EMC and knowledge systems used in this mod.