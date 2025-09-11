# FRC Forklift Simulator

A 2D physics-based forklift simulator for teaching robotics concepts. Control a forklift robot to pick up and score cargo boxes in a scoring zone.

## Project Overview

This simulator features:
- Real-time 2D physics simulation
- Sprite-based graphics with fallback rendering
- Precise collision detection
- Intuitive controls for driving and forklift operation

## Project Structure

The project is organized into several packages:

- `physics/` - Physics engine and collision detection
  - `AABB.java` - Axis-aligned bounding box collision
  - `Body.java` - Physical body with mass and velocity
  - `Vec2.java` - 2D vector operations

- `objects/` - Game objects and entities
  - `Robot.java` - Player-controlled forklift robot
  - `Cargo.java` - Pickupable cargo boxes
  - `Bucket.java` - Scoring zone container

- `ui/` - Graphics and user interface
  - `SimulationPanel.java` - Main game panel and rendering
  - `SpriteLoader.java` - Sprite loading and management

- `core/` - Core game systems
  - `World.java` - Game world and object management
  - `GameObject.java` - Base class for all game objects

## Building and Running

1. Ensure you have Java JDK 8 or later installed
2. Compile using the provided script:
   ```
   .\compile.bat
   ```
   This will compile and run the simulation.

## Controls

- A/D - Drive the robot left/right
- W/S - Lift the forks up/down
- Q/E - Tilt forks forward/back
- Space - Brake
- R - Reset simulation
- P - Pause/Resume

## Gameplay

1. **Driving**
   - Use left/right arrows to position your robot
   - Hold space to brake and stop precisely
   - The robot has realistic mass and momentum

2. **Cargo Pickup**
   - Position forks under a cargo box
   - Keep forks level (-2° to 5° tilt)
   - Lift up to pick up the cargo
   - Cargo will stay on forks while level

3. **Scoring**
   - Drive to the scoring bucket
   - Lower cargo into bucket or tilt to drop
   - Score is shown in the HUD

## Graphics

The game uses sprite-based graphics with graceful fallback to drawn graphics:

- `assets/sprites/` - Contains game sprites:
  - `robot_base.png` - Robot chassis
  - `robot_mast.png` - Lift mast
  - `robot_forks.png` - Fork assembly
  - `cargo.png` - Cargo boxes
  - `bucket.png` - Scoring bucket

If sprites are missing, the game will automatically use fallback drawn graphics.

## Physics System

The simulation includes:
- Realistic gravity and momentum
- Precise collision detection
- Friction and damping effects
- Mass-based physics interactions

## Development

To modify the simulation:

1. **Adding Features**
   - Each object extends `GameObject` or `Body`
   - Use `World` class to manage new objects
   - Add sprites to `assets/sprites/`

2. **Tuning Physics**
   - Adjust constants in `World.java`
   - Modify collision response in objects
   - Change mass and friction values

3. **Graphics Changes**
   - Update sprite loading in `SpriteLoader.java`
   - Modify rendering in object draw() methods
   - Adjust `SimulationPanel` for display changes
