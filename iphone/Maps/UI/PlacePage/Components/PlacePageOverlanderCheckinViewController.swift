class OverlanderCheckinViewController: UIViewController {
  @IBOutlet var dateAndAuthorTextView: UITextView!
  @IBOutlet var commentTextView: UITextView!

  override func viewDidLoad() {
    super.viewDidLoad()

    dateAndAuthorTextView.textContainerInset = .zero
    commentTextView.textContainerInset = .zero
  }
}

class PlacePageOverlanderCheckinViewController: UIViewController {

  @IBOutlet var stackView: UIStackView!

  private var checkin1View: OverlanderCheckinViewController?
  private var checkin2View: OverlanderCheckinViewController?
  private var checkin3View: OverlanderCheckinViewController?

  var placePageInfoData: PlacePageInfoData!
  weak var delegate: PlacePageInfoViewControllerDelegate?

  override func viewDidLoad() {
    _ = placePageInfoData
    
    if (placePageInfoData.overlanderDescription != "") {
      let dateAndAuthor = (placePageInfoData.overlanderCheckin1time)! + " by " + (placePageInfoData.overlanderCheckin1author)!
      checkin1View = createCheckinItem(dateAndAuthor: dateAndAuthor, comment: placePageInfoData.overlanderDescription!)
    }
    
    if (placePageInfoData.overlanderCheckin2comment != "") {
      let dateAndAuthor = (placePageInfoData.overlanderCheckin2time)! + " by " + (placePageInfoData.overlanderCheckin2author)!
      checkin2View = createCheckinItem(dateAndAuthor: dateAndAuthor, comment: placePageInfoData.overlanderCheckin2comment!)
    }
    
    if (placePageInfoData.overlanderCheckin3comment != "") {
      let dateAndAuthor = (placePageInfoData.overlanderCheckin3time)! + " by " + (placePageInfoData.overlanderCheckin3author)!
      checkin3View = createCheckinItem(dateAndAuthor: dateAndAuthor, comment: placePageInfoData.overlanderCheckin3comment!)
    }

  }
  // MARK: private

  private func createCheckinItem(dateAndAuthor: String,
                              comment: String) -> OverlanderCheckinViewController {
    let vc = storyboard!.instantiateViewController(ofType: OverlanderCheckinViewController.self)
    addToStack(vc)
    let font = UIFont.regular14()
    let color = UIColor.blackPrimaryText()
    let paragraphStyle = NSMutableParagraphStyle()
    paragraphStyle.lineSpacing = 4

    let attributedDateAndAuthorString: NSAttributedString
    attributedDateAndAuthorString = NSAttributedString(string: dateAndAuthor as String,
                                            attributes: [NSAttributedString.Key.font : font,
                                                          NSAttributedString.Key.foregroundColor: color,
                                                          NSAttributedString.Key.paragraphStyle: paragraphStyle])
    
    let attributedCommentString: NSAttributedString
    attributedCommentString = NSAttributedString(string: comment as String,
                                            attributes: [NSAttributedString.Key.font : font,
                                                          NSAttributedString.Key.foregroundColor: color,
                                                          NSAttributedString.Key.paragraphStyle: paragraphStyle])
    vc.dateAndAuthorTextView.attributedText = attributedDateAndAuthorString
    vc.commentTextView.attributedText = attributedCommentString
    return vc;
  }

  private func addToStack(_ viewController: UIViewController) {
    addChild(viewController)
    stackView.addArrangedSubview(viewController.view)
    viewController.didMove(toParent: self)
  }
}
