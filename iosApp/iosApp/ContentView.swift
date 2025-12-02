import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let mainViewController = MainViewControllerKt.MainViewController()
        let applicationId = (Bundle.main.object(forInfoDictionaryKey: "APPLICATION_ID") as? String)!
        let specId = (Bundle.main.object(forInfoDictionaryKey: "EXTERNAL_SERVICES_SPEC_ID") as? String)!
        let processId = (Bundle.main.object(forInfoDictionaryKey: "PROCESS_ID") as? String)!
        
        PlatformContext.shared.setupValues(applicationId: applicationId, processId: processId, specId: specId)

        return mainViewController
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}



