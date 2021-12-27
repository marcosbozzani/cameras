using System;

namespace Duck.Cameras.Windows.Model
{
    public class Result<T>
    {
        public static Result<T> Ok(T value)
        {
            Result<T> result = new Result<T>();
            result.Value = value;
            result.Success = true;
            result.Exception = null;
            return result;
        }

        public static Result<T> Error(T value, Exception exception = null)
        {
            Result<T> result = new Result<T>();
            result.Value = value;
            result.Success = false;
            result.Exception = exception;
            return result;
        }

        public bool Success { get; private set; }
        public T Value { get; private set; }
        public Exception Exception { get; private set; }
    }
}
