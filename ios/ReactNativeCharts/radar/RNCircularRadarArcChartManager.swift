//  Created by xudong wu on 23/02/2017.
//  Copyright wuxudong
//

import UIKit

@objc(RNCircularRadarArcChartManager)
@objcMembers
open class RNCircularRadarArcChartManager: RCTViewManager {
  override open func view() -> UIView! {
    let ins = RNCircularRadarChartView()
    return ins;
  }

  override public static func requiresMainQueueSetup() -> Bool {
    return true;
  }

}
