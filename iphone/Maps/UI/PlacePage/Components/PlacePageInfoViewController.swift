class InfoItemViewController: UIViewController {
  enum Style {
    case regular
    case link
  }

  typealias TapHandler = () -> Void

  @IBOutlet var imageView: UIImageView!
  @IBOutlet var infoLabel: UILabel!
  @IBOutlet var accessoryImage: UIImageView!
  @IBOutlet var separatorView: UIView!
  @IBOutlet var tapGestureRecognizer: UITapGestureRecognizer!

  var tapHandler: TapHandler?
  var style: Style = .regular {
    didSet {
      switch style {
      case .regular:
        imageView?.styleName = "MWMBlack"
        infoLabel?.styleName = "blackPrimaryText"
      case .link:
        imageView?.styleName = "MWMBlue"
        infoLabel?.styleName = "linkBlueText"
      }
    }
  }
  var canShowMenu = false

  @IBAction func onTap(_ sender: UITapGestureRecognizer) {
    tapHandler?()
  }

  @IBAction func onLongPress(_ sender: UILongPressGestureRecognizer) {
    guard sender.state == .began, canShowMenu else { return }
    let menuController = UIMenuController.shared
    menuController.setTargetRect(infoLabel.frame, in: self.view)
    infoLabel.becomeFirstResponder()
    menuController.setMenuVisible(true, animated: true)
  }

  override func viewDidLoad() {
    super.viewDidLoad()

    if style == .link {
      imageView.styleName = "MWMBlue"
      infoLabel.styleName = "linkBlueText"
    }
  }
}

protocol PlacePageInfoViewControllerDelegate: AnyObject {
  func didPressCall()
  func didPressWebsite()
  func didPressEmail()
}

class PlacePageInfoViewController: UIViewController {
  private struct Const {
    static let coordinatesKey = "PlacePageInfoViewController_coordinatesKey"
  }
  private typealias TapHandler = InfoItemViewController.TapHandler
  private typealias Style = InfoItemViewController.Style

  @IBOutlet var stackView: UIStackView!

  private lazy var openingHoursView: OpeningHoursViewController = {
    storyboard!.instantiateViewController(ofType: OpeningHoursViewController.self)
  }()

  private var rawOpeningHoursView: InfoItemViewController?
  private var phoneView: InfoItemViewController?
  private var websiteView: InfoItemViewController?
  private var emailView: InfoItemViewController?
  private var cuisineView: InfoItemViewController?
  private var operatorView: InfoItemViewController?
  private var wifiView: InfoItemViewController?
  private var addressView: InfoItemViewController?
  private var coordinatesView: InfoItemViewController?
  private var overlanderDescriptionView: InfoItemViewController?
  private var overlanderOpenView: InfoItemViewController?
  private var overlanderElectricityView: InfoItemViewController?
  private var overlanderWifiView: InfoItemViewController?
  private var overlanderKitchenView: InfoItemViewController?
  private var overlanderParkingView: InfoItemViewController?
  private var overlanderRestaurantView: InfoItemViewController?
  private var overlanderShowersView: InfoItemViewController?
  private var overlanderWaterView: InfoItemViewController?
  private var overlanderToiletsView: InfoItemViewController?
  private var overlanderBigrigfriendlyView: InfoItemViewController?
  private var overlanderTentfriendlyView: InfoItemViewController?
  private var overlanderPetfriendlyView: InfoItemViewController?
  private var overlanderSanidumpView: InfoItemViewController?
  

  var placePageInfoData: PlacePageInfoData!
  weak var delegate: PlacePageInfoViewControllerDelegate?
  var showFormattedCoordinates: Bool {
    get {
      UserDefaults.standard.bool(forKey: Const.coordinatesKey)
    }
    set {
      UserDefaults.standard.set(newValue, forKey: Const.coordinatesKey)
    }
  }
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    _ = placePageInfoData

    if let openingHours = placePageInfoData.openingHours {
      openingHoursView.openingHours = openingHours
      addToStack(openingHoursView)
    } else if let openingHoursString = placePageInfoData.openingHoursString {
      rawOpeningHoursView = createInfoItem(openingHoursString, icon: UIImage(named: "ic_placepage_open_hours"))
      rawOpeningHoursView?.infoLabel.numberOfLines = 0
    }

    if let phone = placePageInfoData.phone {
      var cellStyle: Style = .regular
      if let phoneUrl = placePageInfoData.phoneUrl, UIApplication.shared.canOpenURL(phoneUrl) {
        cellStyle = .link
      }
      phoneView = createInfoItem(phone, icon: UIImage(named: "ic_placepage_phone_number"), style: cellStyle) { [weak self] in
        self?.delegate?.didPressCall()
      }
    }

    if let website = placePageInfoData.website {
      websiteView = createInfoItem(website, icon: UIImage(named: "ic_placepage_website"), style: .link) { [weak self] in
        self?.delegate?.didPressWebsite()
      }
    }

    if let email = placePageInfoData.email {
      emailView = createInfoItem(email, icon: UIImage(named: "ic_placepage_email"), style: .link) { [weak self] in
        self?.delegate?.didPressEmail()
      }
    }

    if let cuisine = placePageInfoData.cuisine {
      cuisineView = createInfoItem(cuisine, icon: UIImage(named: "ic_placepage_cuisine"))
    }

    if let ppOperator = placePageInfoData.ppOperator {
      operatorView = createInfoItem(ppOperator, icon: UIImage(named: "ic_placepage_operator"))
    }
    
    if let overlanderDescription = placePageInfoData.overlanderDescription {
      overlanderDescriptionView = createInfoItem(overlanderDescription, icon: UIImage(named: "ic_placepage_website"))
    }
    
    if let overlanderOpen = placePageInfoData.overlanderOpen {
      overlanderOpenView = createInfoItem(overlanderOpen, icon: UIImage(named: "ic_placepage_overlander_open"))
    }
    
    if let overlanderElectricity = placePageInfoData.overlanderElectricity {
      overlanderElectricityView = createInfoItem(overlanderElectricity, icon: UIImage(named: "ic_placepage_overlander_electricity"))
    }
    
    if let overlanderWifi = placePageInfoData.overlanderWifi {
      overlanderWifiView = createInfoItem(overlanderWifi, icon: UIImage(named: "ic_placepage_wifi"))
    }
    
    if let overlanderKitchen = placePageInfoData.overlanderKitchen {
      overlanderKitchenView = createInfoItem(overlanderKitchen, icon: UIImage(named: "ic_placepage_overlander_kitchen"))
    }
    
    if let overlanderParking = placePageInfoData.overlanderParking {
      overlanderParkingView = createInfoItem(overlanderParking, icon: UIImage(named: "ic_placepage_overlander_parking"))
    }
    
    if let overlanderRestaurant = placePageInfoData.overlanderRestaurant {
      overlanderRestaurantView = createInfoItem(overlanderRestaurant, icon: UIImage(named: "ic_placepage_overlander_restaurant"))
    }
    
    if let overlanderShowers = placePageInfoData.overlanderShowers {
      overlanderShowersView = createInfoItem(overlanderShowers, icon: UIImage(named: "ic_placepage_overlander_showers"))
    }
    
    if let overlanderWater = placePageInfoData.overlanderWater {
      overlanderWaterView = createInfoItem(overlanderWater, icon: UIImage(named: "ic_placepage_overlander_water"))
    }
    
    if let overlanderToilets = placePageInfoData.overlanderToilets {
      overlanderToiletsView = createInfoItem(overlanderToilets, icon: UIImage(named: "ic_placepage_overlander_toilets"))
    }
    
    if let overlanderBigrigfriendly = placePageInfoData.overlanderBigrigfriendly {
      overlanderBigrigfriendlyView = createInfoItem(overlanderBigrigfriendly, icon: UIImage(named: "ic_placepage_overlander_bigrigfriendly"))
    }
    
    if let overlanderTentfriendly = placePageInfoData.overlanderTentfriendly {
      overlanderTentfriendlyView = createInfoItem(overlanderTentfriendly, icon: UIImage(named: "ic_placepage_overlander_tentfriendly"))
    }
    
    if let overlanderPetfriendly = placePageInfoData.overlanderPetfriendly {
      overlanderPetfriendlyView = createInfoItem(overlanderPetfriendly, icon: UIImage(named: "ic_placepage_overlander_petfriendly"))
    }
    
    if let overlanderSanidump = placePageInfoData.overlanderSanidump {
      overlanderSanidumpView = createInfoItem(overlanderSanidump, icon: UIImage(named: "ic_placepage_overlander_sanidump"))
    }

    if placePageInfoData.wifiAvailable {
      wifiView = createInfoItem(L("WiFi_available"), icon: UIImage(named: "ic_placepage_wifi"))
    }

    if let address = placePageInfoData.address {
      addressView = createInfoItem(address, icon: UIImage(named: "ic_placepage_adress"))
      addressView?.canShowMenu = true
    }

    if let formattedCoordinates = placePageInfoData.formattedCoordinates,
      let rawCoordinates = placePageInfoData.rawCoordinates {
      let coordinates = showFormattedCoordinates ? formattedCoordinates : rawCoordinates
      coordinatesView = createInfoItem(coordinates, icon: UIImage(named: "ic_placepage_coordinate")) {
        [unowned self] in
        self.showFormattedCoordinates = !self.showFormattedCoordinates
        let coordinates = self.showFormattedCoordinates ? formattedCoordinates : rawCoordinates
        self.coordinatesView?.infoLabel.text = coordinates
      }
    } else if let formattedCoordinates = placePageInfoData.formattedCoordinates {
      coordinatesView = createInfoItem(formattedCoordinates, icon: UIImage(named: "ic_placepage_coordinate"))
    } else if let rawCoordinates = placePageInfoData.rawCoordinates {
      coordinatesView = createInfoItem(rawCoordinates, icon: UIImage(named: "ic_placepage_coordinate"))
    }

    coordinatesView?.accessoryImage.image = UIImage(named: "ic_placepage_change")
    coordinatesView?.accessoryImage.isHidden = false
    coordinatesView?.canShowMenu = true
  }

  // MARK: private

  private func createInfoItem(_ info: String,
                              icon: UIImage?,
                              style: Style = .regular,
                              tapHandler: TapHandler? = nil) -> InfoItemViewController {
    let vc = storyboard!.instantiateViewController(ofType: InfoItemViewController.self)
    addToStack(vc)
    vc.imageView.image = icon
    vc.infoLabel.text = info
    vc.style = style
    vc.tapHandler = tapHandler
    return vc;
  }

  private func addToStack(_ viewController: UIViewController) {
    addChild(viewController)
    stackView.addArrangedSubview(viewController.view)
    viewController.didMove(toParent: self)
  }
}
