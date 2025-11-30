# PR: Sensors Plugin - Individual Sensor Controls

Summary

- Refactored sensors plugin from polling-all-at-once to individual start/stop/read controls per sensor
- Each sensor (accelerometer, gyroscope, magnetometer, deviceMotion, barometer) now has its own lifecycle
- Native sensors only run when explicitly started, stopping fully unregisters listeners
- Changed polling interval from 100ms to 250ms to reduce resource usage
- Fixed double-wrapped response bug showing "status: Yes, data: Yes" instead of sensor availability

API Change

// Before: All sensors polled together
await sensors.poll();

// After: Individual control
await sensors.accelerometer.start();
const data = await sensors.accelerometer.read();  // { x, y, z }
await sensors.accelerometer.stop();

Changes

- iOS Swift: 15 new BridgeFunction classes (Start/Stop/Read per sensor + IsAvailable)
- Android Kotlin: Same pattern with individual SensorEventListeners per sensor
- nativephp.json: 16 bridge function mappings
- sensors.js: New namespace-based API with individual sensor objects
- Sensors.vue: Individual toggle buttons per sensor card, 250ms polling, cleanup on unmount

Why

- Prevents battery drain from sensors running in background when not needed
- Gives developers fine-grained control over which sensors to use
- Reduces heat buildup from unnecessary sensor polling
