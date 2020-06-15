#### Gojira Sample Project 

This project is intended to share the code samples on how to use Gojira for integrating with 
DropWizard based application.

SampleApp
1. API to test GET /github/usersFlipkartIncubator has been added in SampleAppGithubResource. 
2. API to test POST /httpbin/post has been added in SampleAppHttpBinPostResource.
3. Gojira configurations have been added in SampleAppGojiraModule.
4. SampleAppHttpHelper is used to make external HTTP calls, and @ProfileOrTest annotations have 
been added in their methods.

SampleAppTest
1. Setup is done by starting SampleApp, installing test specific TestExecuteModule, and then calling
setup on Managed class to bootstrap connections necessary for making initiating tests.
2. In testGetGithubUserMeta/testPostHttpBinData, call is made to the SampleApp in PROFILE mode, 
and then test is initiated, test results are read and verified that they are matching as expected.