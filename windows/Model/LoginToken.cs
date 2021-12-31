namespace Duck.Cameras.Windows.Model
{
    public class LoginToken
    {
        public LoginToken(string value)
        {
            Value = value;
        }

        public string Value { get; private set; }

        public bool IsEmpty()
        {
            return string.IsNullOrEmpty(Value);
        }
    }
}
