import Foundation
import CoreMotion

enum SensorsFunctions {

    // Lazy initialization to avoid crashes at app startup
    private static var _motionManager: CMMotionManager?
    private static var _altimeter: CMAltimeter?

    private static var motionManager: CMMotionManager {
        if _motionManager == nil {
            _motionManager = CMMotionManager()
        }
        return _motionManager!
    }

    private static var altimeter: CMAltimeter {
        if _altimeter == nil {
            _altimeter = CMAltimeter()
        }
        return _altimeter!
    }

    // Cached sensor values
    private static var accelerometerData: CMAccelerometerData?
    private static var gyroscopeData: CMGyroData?
    private static var magnetometerData: CMMagnetometerData?
    private static var deviceMotionData: CMDeviceMotion?
    private static var altimeterData: CMAltitudeData?

    // Running state per sensor
    private static var accelerometerRunning = false
    private static var gyroscopeRunning = false
    private static var magnetometerRunning = false
    private static var deviceMotionRunning = false
    private static var barometerRunning = false

    private static let updateInterval = 1.0 / 60.0 // 60 Hz

    // MARK: - Accelerometer

    class AccelerometerStart: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard !SensorsFunctions.accelerometerRunning else {
                return ["success": true, "message": "Already running"]
            }

            guard SensorsFunctions.motionManager.isAccelerometerAvailable else {
                return BridgeResponse.error(code: "NOT_AVAILABLE", message: "Accelerometer not available")
            }

            if !Thread.isMainThread {
                DispatchQueue.main.sync {
                    SensorsFunctions.motionManager.accelerometerUpdateInterval = SensorsFunctions.updateInterval
                    SensorsFunctions.motionManager.startAccelerometerUpdates(to: .main) { data, _ in
                        SensorsFunctions.accelerometerData = data
                    }
                }
            } else {
                SensorsFunctions.motionManager.accelerometerUpdateInterval = SensorsFunctions.updateInterval
                SensorsFunctions.motionManager.startAccelerometerUpdates(to: .main) { data, _ in
                    SensorsFunctions.accelerometerData = data
                }
            }

            SensorsFunctions.accelerometerRunning = true
            return ["success": true]
        }
    }

    class AccelerometerStop: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard SensorsFunctions.accelerometerRunning else {
                return ["success": true, "message": "Not running"]
            }

            if !Thread.isMainThread {
                DispatchQueue.main.sync {
                    SensorsFunctions.motionManager.stopAccelerometerUpdates()
                }
            } else {
                SensorsFunctions.motionManager.stopAccelerometerUpdates()
            }

            SensorsFunctions.accelerometerData = nil
            SensorsFunctions.accelerometerRunning = false
            return ["success": true]
        }
    }

    class AccelerometerRead: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard SensorsFunctions.accelerometerRunning else {
                return BridgeResponse.error(code: "NOT_STARTED", message: "Accelerometer not started. Call start() first.")
            }

            guard let data = SensorsFunctions.accelerometerData else {
                return ["x": 0.0, "y": 0.0, "z": 0.0]
            }

            // CoreMotion returns acceleration in g-force, convert to m/s²
            let gravity = 9.81
            return [
                "x": data.acceleration.x * gravity,
                "y": data.acceleration.y * gravity,
                "z": data.acceleration.z * gravity
            ]
        }
    }

    // MARK: - Gyroscope

    class GyroscopeStart: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard !SensorsFunctions.gyroscopeRunning else {
                return ["success": true, "message": "Already running"]
            }

            guard SensorsFunctions.motionManager.isGyroAvailable else {
                return BridgeResponse.error(code: "NOT_AVAILABLE", message: "Gyroscope not available")
            }

            if !Thread.isMainThread {
                DispatchQueue.main.sync {
                    SensorsFunctions.motionManager.gyroUpdateInterval = SensorsFunctions.updateInterval
                    SensorsFunctions.motionManager.startGyroUpdates(to: .main) { data, _ in
                        SensorsFunctions.gyroscopeData = data
                    }
                }
            } else {
                SensorsFunctions.motionManager.gyroUpdateInterval = SensorsFunctions.updateInterval
                SensorsFunctions.motionManager.startGyroUpdates(to: .main) { data, _ in
                    SensorsFunctions.gyroscopeData = data
                }
            }

            SensorsFunctions.gyroscopeRunning = true
            return ["success": true]
        }
    }

    class GyroscopeStop: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard SensorsFunctions.gyroscopeRunning else {
                return ["success": true, "message": "Not running"]
            }

            if !Thread.isMainThread {
                DispatchQueue.main.sync {
                    SensorsFunctions.motionManager.stopGyroUpdates()
                }
            } else {
                SensorsFunctions.motionManager.stopGyroUpdates()
            }

            SensorsFunctions.gyroscopeData = nil
            SensorsFunctions.gyroscopeRunning = false
            return ["success": true]
        }
    }

    class GyroscopeRead: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard SensorsFunctions.gyroscopeRunning else {
                return BridgeResponse.error(code: "NOT_STARTED", message: "Gyroscope not started. Call start() first.")
            }

            guard let data = SensorsFunctions.gyroscopeData else {
                return ["x": 0.0, "y": 0.0, "z": 0.0]
            }

            return [
                "x": data.rotationRate.x,
                "y": data.rotationRate.y,
                "z": data.rotationRate.z
            ]
        }
    }

    // MARK: - Magnetometer

    class MagnetometerStart: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard !SensorsFunctions.magnetometerRunning else {
                return ["success": true, "message": "Already running"]
            }

            guard SensorsFunctions.motionManager.isMagnetometerAvailable else {
                return BridgeResponse.error(code: "NOT_AVAILABLE", message: "Magnetometer not available")
            }

            if !Thread.isMainThread {
                DispatchQueue.main.sync {
                    SensorsFunctions.motionManager.magnetometerUpdateInterval = SensorsFunctions.updateInterval
                    SensorsFunctions.motionManager.startMagnetometerUpdates(to: .main) { data, _ in
                        SensorsFunctions.magnetometerData = data
                    }
                }
            } else {
                SensorsFunctions.motionManager.magnetometerUpdateInterval = SensorsFunctions.updateInterval
                SensorsFunctions.motionManager.startMagnetometerUpdates(to: .main) { data, _ in
                    SensorsFunctions.magnetometerData = data
                }
            }

            SensorsFunctions.magnetometerRunning = true
            return ["success": true]
        }
    }

    class MagnetometerStop: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard SensorsFunctions.magnetometerRunning else {
                return ["success": true, "message": "Not running"]
            }

            if !Thread.isMainThread {
                DispatchQueue.main.sync {
                    SensorsFunctions.motionManager.stopMagnetometerUpdates()
                }
            } else {
                SensorsFunctions.motionManager.stopMagnetometerUpdates()
            }

            SensorsFunctions.magnetometerData = nil
            SensorsFunctions.magnetometerRunning = false
            return ["success": true]
        }
    }

    class MagnetometerRead: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard SensorsFunctions.magnetometerRunning else {
                return BridgeResponse.error(code: "NOT_STARTED", message: "Magnetometer not started. Call start() first.")
            }

            guard let data = SensorsFunctions.magnetometerData else {
                return ["x": 0.0, "y": 0.0, "z": 0.0]
            }

            return [
                "x": data.magneticField.x,
                "y": data.magneticField.y,
                "z": data.magneticField.z
            ]
        }
    }

    // MARK: - Device Motion

    class DeviceMotionStart: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard !SensorsFunctions.deviceMotionRunning else {
                return ["success": true, "message": "Already running"]
            }

            guard SensorsFunctions.motionManager.isDeviceMotionAvailable else {
                return BridgeResponse.error(code: "NOT_AVAILABLE", message: "Device motion not available")
            }

            if !Thread.isMainThread {
                DispatchQueue.main.sync {
                    SensorsFunctions.motionManager.deviceMotionUpdateInterval = SensorsFunctions.updateInterval
                    SensorsFunctions.motionManager.startDeviceMotionUpdates(to: .main) { data, _ in
                        SensorsFunctions.deviceMotionData = data
                    }
                }
            } else {
                SensorsFunctions.motionManager.deviceMotionUpdateInterval = SensorsFunctions.updateInterval
                SensorsFunctions.motionManager.startDeviceMotionUpdates(to: .main) { data, _ in
                    SensorsFunctions.deviceMotionData = data
                }
            }

            SensorsFunctions.deviceMotionRunning = true
            return ["success": true]
        }
    }

    class DeviceMotionStop: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard SensorsFunctions.deviceMotionRunning else {
                return ["success": true, "message": "Not running"]
            }

            if !Thread.isMainThread {
                DispatchQueue.main.sync {
                    SensorsFunctions.motionManager.stopDeviceMotionUpdates()
                }
            } else {
                SensorsFunctions.motionManager.stopDeviceMotionUpdates()
            }

            SensorsFunctions.deviceMotionData = nil
            SensorsFunctions.deviceMotionRunning = false
            return ["success": true]
        }
    }

    class DeviceMotionRead: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard SensorsFunctions.deviceMotionRunning else {
                return BridgeResponse.error(code: "NOT_STARTED", message: "Device motion not started. Call start() first.")
            }

            guard let data = SensorsFunctions.deviceMotionData else {
                return [
                    "attitude": ["pitch": 0.0, "roll": 0.0, "yaw": 0.0],
                    "rotationRate": ["x": 0.0, "y": 0.0, "z": 0.0],
                    "gravity": ["x": 0.0, "y": 0.0, "z": 0.0]
                ]
            }

            return [
                "attitude": [
                    "pitch": data.attitude.pitch,
                    "roll": data.attitude.roll,
                    "yaw": data.attitude.yaw
                ],
                "rotationRate": [
                    "x": data.rotationRate.x,
                    "y": data.rotationRate.y,
                    "z": data.rotationRate.z
                ],
                "gravity": [
                    "x": data.gravity.x,
                    "y": data.gravity.y,
                    "z": data.gravity.z
                ]
            ]
        }
    }

    // MARK: - Barometer

    class BarometerStart: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard !SensorsFunctions.barometerRunning else {
                return ["success": true, "message": "Already running"]
            }

            guard CMAltimeter.isRelativeAltitudeAvailable() else {
                return BridgeResponse.error(code: "NOT_AVAILABLE", message: "Barometer not available")
            }

            if !Thread.isMainThread {
                DispatchQueue.main.sync {
                    SensorsFunctions.altimeter.startRelativeAltitudeUpdates(to: .main) { data, _ in
                        SensorsFunctions.altimeterData = data
                    }
                }
            } else {
                SensorsFunctions.altimeter.startRelativeAltitudeUpdates(to: .main) { data, _ in
                    SensorsFunctions.altimeterData = data
                }
            }

            SensorsFunctions.barometerRunning = true
            return ["success": true]
        }
    }

    class BarometerStop: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard SensorsFunctions.barometerRunning else {
                return ["success": true, "message": "Not running"]
            }

            if !Thread.isMainThread {
                DispatchQueue.main.sync {
                    SensorsFunctions.altimeter.stopRelativeAltitudeUpdates()
                }
            } else {
                SensorsFunctions.altimeter.stopRelativeAltitudeUpdates()
            }

            SensorsFunctions.altimeterData = nil
            SensorsFunctions.barometerRunning = false
            return ["success": true]
        }
    }

    class BarometerRead: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard SensorsFunctions.barometerRunning else {
                return BridgeResponse.error(code: "NOT_STARTED", message: "Barometer not started. Call start() first.")
            }

            guard let data = SensorsFunctions.altimeterData else {
                return ["pressure": 0.0, "altitude": 0.0]
            }

            return [
                "pressure": data.pressure.doubleValue * 10.0,
                "altitude": data.relativeAltitude.doubleValue
            ]
        }
    }

    // MARK: - Availability Check

    class IsAvailable: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            let mm = CMMotionManager()

            return [
                "accelerometer": mm.isAccelerometerAvailable,
                "gyroscope": mm.isGyroAvailable,
                "magnetometer": mm.isMagnetometerAvailable,
                "deviceMotion": mm.isDeviceMotionAvailable,
                "barometer": CMAltimeter.isRelativeAltitudeAvailable()
            ]
        }
    }
}
